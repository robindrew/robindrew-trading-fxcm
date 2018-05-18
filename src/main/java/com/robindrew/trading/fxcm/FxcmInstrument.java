package com.robindrew.trading.fxcm;

import static com.robindrew.trading.Instruments.AUD_CAD;
import static com.robindrew.trading.Instruments.AUD_CHF;
import static com.robindrew.trading.Instruments.AUD_JPY;
import static com.robindrew.trading.Instruments.AUD_NZD;
import static com.robindrew.trading.Instruments.AUD_USD;
import static com.robindrew.trading.Instruments.CAD_CHF;
import static com.robindrew.trading.Instruments.DOW_JONES_30;
import static com.robindrew.trading.Instruments.EUR_AUD;
import static com.robindrew.trading.Instruments.EUR_CHF;
import static com.robindrew.trading.Instruments.EUR_GBP;
import static com.robindrew.trading.Instruments.EUR_JPY;
import static com.robindrew.trading.Instruments.EUR_USD;
import static com.robindrew.trading.Instruments.FTSE_100;
import static com.robindrew.trading.Instruments.GBP_CAD;
import static com.robindrew.trading.Instruments.GBP_CHF;
import static com.robindrew.trading.Instruments.GBP_JPY;
import static com.robindrew.trading.Instruments.GBP_NZD;
import static com.robindrew.trading.Instruments.GBP_USD;
import static com.robindrew.trading.Instruments.NZD_CAD;
import static com.robindrew.trading.Instruments.NZD_CHF;
import static com.robindrew.trading.Instruments.NZD_JPY;
import static com.robindrew.trading.Instruments.NZD_USD;
import static com.robindrew.trading.Instruments.USD_CAD;
import static com.robindrew.trading.Instruments.USD_CHF;
import static com.robindrew.trading.Instruments.USD_JPY;
import static com.robindrew.trading.Instruments.US_CRUDE_OIL;

import java.util.LinkedHashMap;
import java.util.Map;

import com.robindrew.common.lang.reflect.field.FieldLister;
import com.robindrew.common.lang.reflect.field.IField;
import com.robindrew.common.lang.reflect.field.IFieldLister;
import com.robindrew.common.util.Check;
import com.robindrew.trading.IInstrument;
import com.robindrew.trading.Instrument;
import com.robindrew.trading.Instruments;
import com.robindrew.trading.price.precision.IPricePrecision;
import com.robindrew.trading.price.precision.PricePrecision;
import com.robindrew.trading.provider.ITradingProvider;
import com.robindrew.trading.provider.TradingProvider;

public class FxcmInstrument extends Instrument implements IFxcmInstrument {

	/** AUD/USD. */
	public static final FxcmInstrument SPOT_AUD_USD = new FxcmInstrument("AUD/USD", AUD_USD, 6);
	/** AUD/CAD. */
	public static final FxcmInstrument SPOT_AUD_CAD = new FxcmInstrument("AUD/CAD", AUD_CAD, 1);
	/** AUD/CHF. */
	public static final FxcmInstrument SPOT_AUD_CHF = new FxcmInstrument("AUD/CHF", AUD_CHF, 1);
	/** AUD/JPY. */
	public static final FxcmInstrument SPOT_AUD_JPY = new FxcmInstrument("AUD/JPY", AUD_JPY, 1);
	/** AUD/NZD. */
	public static final FxcmInstrument SPOT_AUD_NZD = new FxcmInstrument("AUD/NZD", AUD_NZD, 1);
	/** CAD/CHF. */
	public static final FxcmInstrument SPOT_CAD_CHF = new FxcmInstrument("CAD/CHF", CAD_CHF, 1);
	/** EUR/AUD. */
	public static final FxcmInstrument SPOT_EUR_AUD = new FxcmInstrument("EUR/AUD", EUR_AUD, 1);
	/** EUR/CHF. */
	public static final FxcmInstrument SPOT_EUR_CHF = new FxcmInstrument("EUR/CHF", EUR_CHF, 1);
	/** EUR/GBP. */
	public static final FxcmInstrument SPOT_EUR_GBP = new FxcmInstrument("EUR/GBP", EUR_GBP, 1);
	/** EUR/JPY. */
	public static final FxcmInstrument SPOT_EUR_JPY = new FxcmInstrument("EUR/JPY", EUR_JPY, 1);
	/** EUR/USD. */
	public static final FxcmInstrument SPOT_EUR_USD = new FxcmInstrument("EUR/USD", EUR_USD, 6);
	/** GBP/CAD. */
	public static final FxcmInstrument SPOT_GBP_CAD = new FxcmInstrument("GBP/CAD", GBP_CAD, 6);
	/** GBP/CHF. */
	public static final FxcmInstrument SPOT_GBP_CHF = new FxcmInstrument("GBP/CHF", GBP_CHF, 6);
	/** GBP/JPY. */
	public static final FxcmInstrument SPOT_GBP_JPY = new FxcmInstrument("GBP/JPY", GBP_JPY, 4);
	/** GBP/NZD. */
	public static final FxcmInstrument SPOT_GBP_NZD = new FxcmInstrument("GBP/NZD", GBP_NZD, 1);
	/** GBP/USD. */
	public static final FxcmInstrument SPOT_GBP_USD = new FxcmInstrument("GBP/USD", GBP_USD, 6);
	/** NZD/CAD. */
	public static final FxcmInstrument SPOT_NZD_CAD = new FxcmInstrument("NZD/CAD", NZD_CAD, 1);
	/** NZD/CHF. */
	public static final FxcmInstrument SPOT_NZD_CHF = new FxcmInstrument("NZD/CHF", NZD_CHF, 1);
	/** NZD/JPY. */
	public static final FxcmInstrument SPOT_NZD_JPY = new FxcmInstrument("NZD/JPY", NZD_JPY, 1);
	/** NZD/USD. */
	public static final FxcmInstrument SPOT_NZD_USD = new FxcmInstrument("NZD/USD", NZD_USD, 5);
	/** USD/CAD. */
	public static final FxcmInstrument SPOT_USD_CAD = new FxcmInstrument("USD/CAD", USD_CAD, 6);
	/** USD/CHF. */
	public static final FxcmInstrument SPOT_USD_CHF = new FxcmInstrument("USD/CHF", USD_CHF, 6);
	/** USD/JPY. */
	public static final FxcmInstrument SPOT_USD_JPY = new FxcmInstrument("USD/JPY", USD_JPY, 4);

	/** XAU/USD. */
	public static final FxcmInstrument SPOT_GOLD = new FxcmInstrument("XAU/USD", Instruments.GOLD, 3);
	/** XAU/USD. */
	public static final FxcmInstrument SPOT_SILVER = new FxcmInstrument("XAG/USD", Instruments.GOLD, 3);

	/** US30. */
	public static final FxcmInstrument SPOT_DOW_JONES = new FxcmInstrument("US30", DOW_JONES_30, 2);
	/** UK100. */
	public static final FxcmInstrument SPOT_FTSE_100 = new FxcmInstrument("UK100", FTSE_100, 2);

	/** US CRUDE. */
	public static final FxcmInstrument SPOT_US_CRUDE = new FxcmInstrument("USOil", US_CRUDE_OIL, 4);

	private static final Map<String, FxcmInstrument> cache = populateCache();

	private static Map<String, FxcmInstrument> populateCache() {
		Map<String, FxcmInstrument> map = new LinkedHashMap<>();

		IFieldLister lister = new FieldLister().includeStatic(true).includeFinal(true).setAccessible(true);
		for (IField field : lister.getFieldList(FxcmInstrument.class)) {
			if (field.isType(FxcmInstrument.class)) {
				FxcmInstrument instrument = field.get(null);
				map.put(field.getName(), instrument);
				map.put(instrument.getName(), instrument);
				map.put(instrument.getSymbol(), instrument);
				map.put(instrument.getUnderlying(true).getName(), instrument);
			}
		}
		return map;
	}

	public static FxcmInstrument valueOf(String name) {
		Check.notEmpty("name", name);
		FxcmInstrument instrument = cache.get(name);
		if (instrument == null) {
			throw new IllegalArgumentException("Unknown instrument: '" + name + "'");
		}
		return instrument;
	}

	private final IPricePrecision precision;

	public FxcmInstrument(String name, IInstrument underlying, int decimalPlaces) {
		super(name, underlying);
		this.precision = new PricePrecision(decimalPlaces);
	}

	@Override
	public String getSymbol() {
		return getName();
	}

	@Override
	public IPricePrecision getPrecision() {
		return precision;
	}

	@Override
	public ITradingProvider getProvider() {
		return TradingProvider.FXCM;
	}

}
