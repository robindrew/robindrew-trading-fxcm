package com.robindrew.trading.fxcm;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

import com.fxcm.fix.Parameter;
import com.fxcm.fix.pretrade.TradingSessionStatus;
import com.robindrew.trading.fxcm.line.FxcmCandleLineParser;
import com.robindrew.trading.fxcm.line.FxcmTickLineParser;
import com.robindrew.trading.price.candle.IPriceCandle;
import com.robindrew.trading.price.candle.line.parser.IPriceCandleLineParser;
import com.robindrew.trading.price.candle.line.parser.PriceCandleLineFile;

/**
 * FXCM Utility Class.
 */
public class Fxcm {

	public static BigDecimal getBaseUnitSize(TradingSessionStatus status) {
		Parameter parameter = status.getParameter("BASE_UNIT_SIZE");
		return new BigDecimal(parameter.getValue());
	}

	public static List<IPriceCandle> readCandlesFromFile(File file, FxcmInstrument instrument) {
		IPriceCandleLineParser parser = new FxcmCandleLineParser(instrument);
		PriceCandleLineFile lineFile = new PriceCandleLineFile(file, parser);
		return lineFile.toList();
	}

	public static List<IPriceCandle> readTicksFromFile(File file, FxcmInstrument instrument) {
		IPriceCandleLineParser parser = new FxcmTickLineParser(instrument);
		PriceCandleLineFile lineFile = new PriceCandleLineFile(file, parser);
		return lineFile.toList();
	}

}
