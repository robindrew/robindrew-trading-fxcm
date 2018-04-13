package com.robindrew.trading.fxcm.platform.rest;

import static com.robindrew.trading.trade.TradeDirection.BUY;
import static com.robindrew.trading.trade.TradeDirection.SELL;

import java.time.LocalDateTime;

import com.fxcm.fix.ISide;
import com.fxcm.fix.Instrument;
import com.fxcm.fix.SideFactory;
import com.fxcm.fix.UTCTimestamp;
import com.robindrew.common.date.Dates;
import com.robindrew.common.util.Java;
import com.robindrew.trading.fxcm.FxcmInstrument;
import com.robindrew.trading.trade.TradeDirection;

public class FxcmRest {

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

}
