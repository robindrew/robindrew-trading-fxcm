package com.robindrew.trading.provider.fxcm.data.tick;

import static java.time.format.DateTimeFormatter.ofPattern;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.base.Stopwatch;
import com.robindrew.common.date.Dates;
import com.robindrew.common.text.Strings;
import com.robindrew.common.text.tokenizer.CharDelimiters;
import com.robindrew.common.text.tokenizer.CharTokenizer;
import com.robindrew.common.util.Java;
import com.robindrew.trading.price.candle.format.pcf.FloatingPoint;
import com.robindrew.trading.price.tick.IPriceTick;
import com.robindrew.trading.price.tick.PriceTick;
import com.robindrew.trading.provider.fxcm.FxcmInstrument;

public class FxcmTickFile {

	private static final Logger log = LoggerFactory.getLogger(FxcmTickFile.class);

	private static DateTimeFormatter DATE_FORMAT = ofPattern("MM/dd/yyyy");
	private static DateTimeFormatter TIME_FORMAT = ofPattern("HH:mm:ss.SSS");

	public static final CharDelimiters DELIMITERS = new CharDelimiters().whitespace().character(',');

	public static long toMillis(LocalDateTime date) {
		return Dates.toMillis(date, ZoneOffset.UTC);
	}

	private final File file;
	private final FxcmInstrument instrument;
	/** Apparently they used UTF16 little endian. I wonder why? */
	private Charset charset = Charsets.UTF_16LE;

	public FxcmTickFile(File file, FxcmInstrument instrument) {
		this.file = file;
		this.instrument = instrument;
	}

	public List<IPriceTick> readToList() {
		log.info("Reading ticks from {}", file.getName());
		Stopwatch timer = Stopwatch.createStarted();
		try (FileInputStream fileInput = new FileInputStream(file)) {
			InputStream input = fileInput;

			// GZIP file?
			if (file.getName().endsWith(".gz")) {
				input = new GZIPInputStream(fileInput);
			}

			// Read candles
			List<IPriceTick> list = readToList(input);
			timer.stop();
			log.info("Read {} ticks from {} in {}", Strings.number(list), file.getName(), timer);
			return list;

		} catch (Exception e) {
			throw Java.propagate(e);
		}
	}

	private List<IPriceTick> readToList(InputStream input) throws IOException {
		List<IPriceTick> list = new ArrayList<>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(input, charset));
		while (true) {
			String line = reader.readLine();
			if (line == null) {
				break;
			}

			IPriceTick tick = parsePriceTick(line);
			if (tick != null) {
				list.add(tick);
			}
		}

		return list;
	}

	private IPriceTick parsePriceTick(String line) {
		line = line.trim();
		if (line.isEmpty() || line.startsWith("DateTime")) {
			return null;
		}

		CharTokenizer tokenizer = new CharTokenizer(line, DELIMITERS);
		int decimalPlaces = instrument.getPricePrecision().getDecimalPlaces();

		// Dates
		LocalDate date = LocalDate.parse(tokenizer.next(false), DATE_FORMAT);
		LocalTime time = LocalTime.parse(tokenizer.next(false), TIME_FORMAT);
		long timestamp = toMillis(LocalDateTime.of(date, time));

		// Prices
		BigDecimal bid = new BigDecimal(tokenizer.next(false));
		BigDecimal ask = new BigDecimal(tokenizer.next(false));

		int bidPrice = FloatingPoint.toBigInt(bid, decimalPlaces);
		int askPrice = FloatingPoint.toBigInt(ask, decimalPlaces);

		return new PriceTick(bidPrice, askPrice, timestamp, decimalPlaces);
	}

}
