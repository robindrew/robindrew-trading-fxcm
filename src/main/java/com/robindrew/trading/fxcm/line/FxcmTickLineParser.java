package com.robindrew.trading.fxcm.line;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.google.common.base.Charsets;
import com.robindrew.common.text.tokenizer.CharTokenizer;
import com.robindrew.trading.fxcm.FxcmInstrument;
import com.robindrew.trading.price.candle.IPriceCandle;
import com.robindrew.trading.price.candle.line.parser.IPriceCandleLineParser;
import com.robindrew.trading.price.candle.tick.TickPriceCandle;
import com.robindrew.trading.price.decimal.Decimals;
import com.robindrew.trading.price.precision.IPricePrecision;

public class FxcmTickLineParser extends FxcmLineParser implements IPriceCandleLineParser {

	public FxcmTickLineParser(int decimalPlaces) {
		super(decimalPlaces);
	}

	public FxcmTickLineParser(IPricePrecision precision) {
		this(precision.getDecimalPlaces());
	}

	public FxcmTickLineParser(FxcmInstrument instrument) {
		this(instrument.getPrecision());
	}

	public Charset getCharset() {
		return Charsets.UTF_16LE;
	}

	@Override
	public IPriceCandle parseCandle(String line) {

		CharTokenizer tokenizer = new CharTokenizer(line, DELIMITERS);
		int decimalPlaces = getDecimalPlaces();

		// Dates
		LocalDate date = LocalDate.parse(tokenizer.next(false), DATE_FORMAT);
		LocalTime time = LocalTime.parse(tokenizer.next(false), TIME_FORMAT);
		long timestamp = toMillis(LocalDateTime.of(date, time));

		// Prices
		BigDecimal bid = new BigDecimal(tokenizer.next(false));
		BigDecimal ask = new BigDecimal(tokenizer.next(false));

		int bidPrice = Decimals.toBigInt(bid, decimalPlaces);
		int askPrice = Decimals.toBigInt(ask, decimalPlaces);

		return new TickPriceCandle(bidPrice, askPrice, timestamp, decimalPlaces);
	}
}
