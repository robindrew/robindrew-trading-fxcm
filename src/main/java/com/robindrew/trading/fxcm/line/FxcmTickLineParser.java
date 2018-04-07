package com.robindrew.trading.fxcm.line;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.robindrew.common.text.tokenizer.CharTokenizer;
import com.robindrew.trading.fxcm.FxcmInstrument;
import com.robindrew.trading.price.decimal.Decimals;
import com.robindrew.trading.price.precision.IPricePrecision;
import com.robindrew.trading.price.tick.IPriceTick;
import com.robindrew.trading.price.tick.PriceTick;
import com.robindrew.trading.price.tick.line.parser.IPriceTickLineParser;

public class FxcmTickLineParser extends FxcmLineParser implements IPriceTickLineParser {

	public FxcmTickLineParser(int decimalPlaces) {
		super(decimalPlaces);
	}

	public FxcmTickLineParser(IPricePrecision precision) {
		this(precision.getDecimalPlaces());
	}

	public FxcmTickLineParser(FxcmInstrument instrument) {
		this(instrument.getPricePrecision());
	}

	@Override
	public IPriceTick parseTick(String line) {

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

		return new PriceTick(bidPrice, askPrice, timestamp, decimalPlaces);
	}
}
