package com.robindrew.trading.fxcm.platform.rest;

import static com.robindrew.trading.price.decimal.Decimals.toInt;
import static com.robindrew.trading.trade.TradeDirection.BUY;
import static com.robindrew.trading.trade.TradeDirection.SELL;
import static java.math.RoundingMode.HALF_UP;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fxcm.fix.ISide;
import com.fxcm.fix.Instrument;
import com.fxcm.fix.SideFactory;
import com.fxcm.fix.UTCTimestamp;
import com.fxcm.fix.pretrade.MarketDataSnapshot;
import com.robindrew.common.date.Dates;
import com.robindrew.common.util.Java;
import com.robindrew.trading.fxcm.FxcmInstrument;
import com.robindrew.trading.price.candle.IPriceCandle;
import com.robindrew.trading.price.candle.PriceCandle;
import com.robindrew.trading.price.candle.TickPriceCandle;
import com.robindrew.trading.trade.TradeDirection;

public class FxcmRest {

	public static IPriceCandle toPriceCandle(MarketDataSnapshot snapshot) throws Exception {

		// Instrument
		FxcmInstrument instrument = toFxcmInstrument(snapshot.getInstrument());

		// Decimal Places
		int decimalPlaces = instrument.getPricePrecision().getDecimalPlaces();

		// Bid Prices
		int bidOpenPrice = toInt(snapshot.getBidOpen(), decimalPlaces);
		int bidHighPrice = toInt(snapshot.getBidHigh(), decimalPlaces);
		int bidLowPrice = toInt(snapshot.getBidLow(), decimalPlaces);
		int bidClosePrice = toInt(snapshot.getBidClose(), decimalPlaces);

		// Ask Prices
		int askOpenPrice = toInt(snapshot.getAskOpen(), decimalPlaces);
		int askHighPrice = toInt(snapshot.getAskHigh(), decimalPlaces);
		int askLowPrice = toInt(snapshot.getAskLow(), decimalPlaces);
		int askClosePrice = toInt(snapshot.getAskClose(), decimalPlaces);

		// Timestamps (UTC)
		long openTime = snapshot.getOpenTimestamp().getTime();
		long closeTime = snapshot.getCloseTimestamp().getTime();

		// Is this a tick?
		if (openTime == closeTime) {
			if ((bidOpenPrice == bidClosePrice) && (bidHighPrice == bidLowPrice)) {
				if ((askOpenPrice == askClosePrice) && (askHighPrice == askLowPrice)) {
					return new TickPriceCandle(bidClosePrice, askClosePrice, closeTime, decimalPlaces);
				}
			}
		}

		// Nope, just a big fat candle!
		return new PriceCandle(bidOpenPrice, bidHighPrice, bidLowPrice, bidClosePrice, askOpenPrice, askHighPrice, askLowPrice, askClosePrice, openTime, closeTime, decimalPlaces);
	}

	public static FxcmInstrument toFxcmInstrument(Instrument instrument) {
		try {
			String symbol = instrument.getSymbol();
			return FxcmInstrument.valueOf(symbol);
		} catch (Exception e) {
			throw Java.propagate(e);
		}
	}

	public static TradeDirection toDirection(ISide side) {
		return side.equals(SideFactory.BUY) ? BUY : SELL;
	}

	public static LocalDateTime toLocalDateTime(UTCTimestamp timestamp) {
		return Dates.toLocalDateTime(timestamp.getTime());
	}

	public static BigDecimal toBigDecimal(double value) {
		return new BigDecimal(value).setScale(10, HALF_UP).stripTrailingZeros();
	}

	public static ISide toSide(TradeDirection direction) {
		if (direction.isBuy()) {
			return SideFactory.BUY;
		} else {
			return SideFactory.SELL;
		}
	}
}
