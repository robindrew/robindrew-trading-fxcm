package com.robindrew.trading.provider.fxcm.data.m1;

import static com.robindrew.trading.price.candle.interval.TimeUnitInterval.ONE_MINUTE;
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
import com.robindrew.trading.price.Mid;
import com.robindrew.trading.price.candle.IPriceCandle;
import com.robindrew.trading.price.candle.PriceCandle;
import com.robindrew.trading.price.candle.format.pcf.FloatingPoint;
import com.robindrew.trading.provider.fxcm.FxcmInstrument;

public class FxcmM1File {

	private static final Logger log = LoggerFactory.getLogger(FxcmM1File.class);

	private static DateTimeFormatter DATE_FORMAT = ofPattern("MM/dd/yyyy");
	private static DateTimeFormatter TIME_FORMAT = ofPattern("HH:mm:ss.SSS");

	public static final CharDelimiters DELIMITERS = new CharDelimiters().whitespace().character(',');

	public static long toMillis(LocalDateTime date) {
		return Dates.toMillis(date, ZoneOffset.UTC);
	}

	private final File file;
	private final FxcmInstrument instrument;
	/** Apparently they used UTF16 little endian. I wonder why? */
	private Charset charset = Charsets.UTF_8;

	public FxcmM1File(File file, FxcmInstrument instrument) {
		this.file = file;
		this.instrument = instrument;
	}

	public List<IPriceCandle> readToList() {
		log.info("Reading candles from {}", file.getName());
		Stopwatch timer = Stopwatch.createStarted();
		try (FileInputStream fileInput = new FileInputStream(file)) {
			InputStream input = fileInput;

			// GZIP file?
			if (file.getName().endsWith(".gz")) {
				input = new GZIPInputStream(fileInput);
			}

			// Read candles
			List<IPriceCandle> list = readToList(input);
			timer.stop();
			log.info("Read {} candles from {} in {}", Strings.number(list), file.getName(), timer);
			return list;

		} catch (Exception e) {
			throw Java.propagate(e);
		}
	}

	private List<IPriceCandle> readToList(InputStream input) throws IOException {
		List<IPriceCandle> list = new ArrayList<>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(input, charset));
		while (true) {
			String line = reader.readLine();
			if (line == null) {
				break;
			}

			IPriceCandle candle = parsePriceCandle(line);
			if (candle != null) {
				list.add(candle);
			}
		}

		return list;
	}

	private IPriceCandle parsePriceCandle(String line) {
		line = line.trim();
		if (line.isEmpty() || line.startsWith("DateTime")) {
			return null;
		}

		CharTokenizer tokenizer = new CharTokenizer(line, DELIMITERS);
		int decimalPlaces = instrument.getPricePrecision().getDecimalPlaces();

		// Dates
		LocalDate date = LocalDate.parse(tokenizer.next(false), DATE_FORMAT);
		LocalTime time = LocalTime.parse(tokenizer.next(false), TIME_FORMAT);
		long openTime = toMillis(LocalDateTime.of(date, time));
		long closeTime = openTime + ONE_MINUTE.getIntervalInMillis();

		// Bid
		BigDecimal bidOpen = new BigDecimal(tokenizer.next(false));
		BigDecimal bidHigh = new BigDecimal(tokenizer.next(false));
		BigDecimal bidLow = new BigDecimal(tokenizer.next(false));
		BigDecimal bidClose = new BigDecimal(tokenizer.next(false));

		// Ask
		BigDecimal askOpen = new BigDecimal(tokenizer.next(false));
		BigDecimal askHigh = new BigDecimal(tokenizer.next(false));
		BigDecimal askLow = new BigDecimal(tokenizer.next(false));
		BigDecimal askClose = new BigDecimal(tokenizer.next(false));

		int openPrice = parsePrice(bidOpen, askOpen, decimalPlaces);
		int highPrice = parsePrice(bidHigh, askHigh, decimalPlaces);
		int lowPrice = parsePrice(bidLow, askLow, decimalPlaces);
		int closePrice = parsePrice(bidClose, askClose, decimalPlaces);

		return new PriceCandle(openPrice, highPrice, lowPrice, closePrice, openTime, closeTime, decimalPlaces);
	}

	private int parsePrice(BigDecimal bid, BigDecimal ask, int decimalPlaces) {
		int bidPrice = FloatingPoint.toBigInt(bid, decimalPlaces);
		int askPrice = FloatingPoint.toBigInt(ask, decimalPlaces);
		return Mid.getMid(bidPrice, askPrice);
	}

}
