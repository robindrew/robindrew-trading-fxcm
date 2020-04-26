package com.robindrew.trading.fxcm.platform.api.java;

import static com.robindrew.trading.price.decimal.Decimals.doubleToInt;
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
import com.robindrew.trading.fxcm.IFxcmInstrument;
import com.robindrew.trading.price.candle.IPriceCandle;
import com.robindrew.trading.price.candle.PriceCandle;
import com.robindrew.trading.price.candle.tick.TickPriceCandle;
import com.robindrew.trading.trade.TradeDirection;

public class FxcmJava {

	public static IPriceCandle toPriceCandle(MarketDataSnapshot snapshot) throws Exception {

		// Instrument
		IFxcmInstrument instrument = toFxcmInstrument(snapshot.getInstrument());

		// Decimal Places
		int decimalPlaces = instrument.getPrecision().getDecimalPlaces();

		// Bid Prices
		int bidOpenPrice = doubleToInt(snapshot.getBidOpen(), decimalPlaces);
		int bidHighPrice = doubleToInt(snapshot.getBidHigh(), decimalPlaces);
		int bidLowPrice = doubleToInt(snapshot.getBidLow(), decimalPlaces);
		int bidClosePrice = doubleToInt(snapshot.getBidClose(), decimalPlaces);

		// Ask Prices
		int askOpenPrice = doubleToInt(snapshot.getAskOpen(), decimalPlaces);
		int askHighPrice = doubleToInt(snapshot.getAskHigh(), decimalPlaces);
		int askLowPrice = doubleToInt(snapshot.getAskLow(), decimalPlaces);
		int askClosePrice = doubleToInt(snapshot.getAskClose(), decimalPlaces);

		// Timestamps (UTC)
		long openTime = snapshot.getOpenTimestamp().getTime();
		long closeTime = snapshot.getCloseTimestamp().getTime();

		// Tick Volume
		long tickVolume = snapshot.getTickVolume();

		// Is this a tick?
		if (openTime == closeTime) {
			if ((bidOpenPrice == bidClosePrice) && (bidHighPrice == bidLowPrice)) {
				if ((askOpenPrice == askClosePrice) && (askHighPrice == askLowPrice)) {
					return new TickPriceCandle(bidClosePrice, askClosePrice, closeTime, decimalPlaces);
				}
			}
		}

		// Nope, just a big fat candle!
		return new PriceCandle(bidOpenPrice, bidHighPrice, bidLowPrice, bidClosePrice, askOpenPrice, askHighPrice, askLowPrice, askClosePrice, openTime, closeTime, decimalPlaces, tickVolume);
	}

	public static IFxcmInstrument toFxcmInstrument(Instrument instrument) {
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
