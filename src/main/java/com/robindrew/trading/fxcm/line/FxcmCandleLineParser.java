package com.robindrew.trading.fxcm.line;

import static com.robindrew.trading.price.candle.interval.TimeUnitInterval.ONE_MINUTE;
import static com.robindrew.trading.price.decimal.Decimals.toBigInt;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.robindrew.common.text.tokenizer.CharTokenizer;
import com.robindrew.trading.fxcm.FxcmInstrument;
import com.robindrew.trading.price.candle.IPriceCandle;
import com.robindrew.trading.price.candle.PriceCandle;
import com.robindrew.trading.price.candle.line.parser.IPriceCandleLineParser;
import com.robindrew.trading.price.precision.IPricePrecision;

public class FxcmCandleLineParser extends FxcmLineParser implements IPriceCandleLineParser {

	public FxcmCandleLineParser(int decimalPlaces) {
		super(decimalPlaces);
	}

	public FxcmCandleLineParser(IPricePrecision precision) {
		this(precision.getDecimalPlaces());
	}

	public FxcmCandleLineParser(FxcmInstrument instrument) {
		this(instrument.getPricePrecision());
	}

	@Override
	public IPriceCandle parseCandle(String line) {

		CharTokenizer tokenizer = new CharTokenizer(line, DELIMITERS);
		int decimalPlaces = getDecimalPlaces();

		// Dates
		LocalDate date = LocalDate.parse(tokenizer.next(false), DATE_FORMAT);
		LocalTime time = LocalTime.parse(tokenizer.next(false), TIME_FORMAT);
		long openTime = toMillis(LocalDateTime.of(date, time));
		long closeTime = openTime + ONE_MINUTE.getIntervalInMillis();

		// Bid
		int bidOpen = toBigInt(tokenizer.next(false), decimalPlaces);
		int bidHigh = toBigInt(tokenizer.next(false), decimalPlaces);
		int bidLow = toBigInt(tokenizer.next(false), decimalPlaces);
		int bidClose = toBigInt(tokenizer.next(false), decimalPlaces);

		// Ask
		int askOpen = toBigInt(tokenizer.next(false), decimalPlaces);
		int askHigh = toBigInt(tokenizer.next(false), decimalPlaces);
		int askLow = toBigInt(tokenizer.next(false), decimalPlaces);
		int askClose = toBigInt(tokenizer.next(false), decimalPlaces);

		return new PriceCandle(bidOpen, bidHigh, bidLow, bidClose, askOpen, askHigh, askLow, askClose, openTime, closeTime, decimalPlaces);
	}

}
