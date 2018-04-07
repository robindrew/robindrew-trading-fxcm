package com.robindrew.trading.fxcm.line;

import static com.robindrew.trading.price.candle.interval.TimeUnitInterval.ONE_MINUTE;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.robindrew.common.text.tokenizer.CharTokenizer;
import com.robindrew.trading.fxcm.FxcmInstrument;
import com.robindrew.trading.price.Mid;
import com.robindrew.trading.price.candle.IPriceCandle;
import com.robindrew.trading.price.candle.PriceCandle;
import com.robindrew.trading.price.candle.line.parser.IPriceCandleLineParser;
import com.robindrew.trading.price.decimal.Decimals;
import com.robindrew.trading.price.precision.IPricePrecision;

public class FxcmCandleLineParser extends FxcmLineParser implements IPriceCandleLineParser{

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
		int bidPrice = Decimals.toBigInt(bid, decimalPlaces);
		int askPrice = Decimals.toBigInt(ask, decimalPlaces);
		return Mid.getMid(bidPrice, askPrice);
	}

}
