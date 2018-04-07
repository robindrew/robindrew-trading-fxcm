package com.robindrew.trading.fxcm;

import java.io.File;
import java.util.List;

import com.robindrew.trading.fxcm.line.FxcmCandleLineParser;
import com.robindrew.trading.fxcm.line.FxcmTickLineParser;
import com.robindrew.trading.price.candle.IPriceCandle;
import com.robindrew.trading.price.candle.line.parser.IPriceCandleLineParser;
import com.robindrew.trading.price.candle.line.parser.PriceCandleLineFile;
import com.robindrew.trading.price.tick.IPriceTick;
import com.robindrew.trading.price.tick.line.parser.IPriceTickLineParser;
import com.robindrew.trading.price.tick.line.parser.PriceTickLineFile;

/**
 * FXCM Utility Class.
 */
public class Fxcm {

	public static List<IPriceCandle> readCandlesFromFile(File file, FxcmInstrument instrument) {
		IPriceCandleLineParser parser = new FxcmCandleLineParser(instrument);
		PriceCandleLineFile lineFile = new PriceCandleLineFile(file, parser);
		return lineFile.toList();
	}

	public static List<IPriceTick> readTicksFromFile(File file, FxcmInstrument instrument) {
		IPriceTickLineParser parser = new FxcmTickLineParser(instrument);
		PriceTickLineFile lineFile = new PriceTickLineFile(file, parser);
		return lineFile.toList();
	}

}
