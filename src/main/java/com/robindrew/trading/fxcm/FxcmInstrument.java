package com.robindrew.trading.fxcm;

import static com.robindrew.trading.Instruments.ASX_200;
import static com.robindrew.trading.Instruments.AUD_CAD;
import static com.robindrew.trading.Instruments.AUD_CHF;
import static com.robindrew.trading.Instruments.AUD_JPY;
import static com.robindrew.trading.Instruments.AUD_NZD;
import static com.robindrew.trading.Instruments.AUD_USD;
import static com.robindrew.trading.Instruments.BRENT_CRUDE_OIL;
import static com.robindrew.trading.Instruments.BTC_USD;
import static com.robindrew.trading.Instruments.CAC_40;
import static com.robindrew.trading.Instruments.CAD_CHF;
import static com.robindrew.trading.Instruments.CAD_JPY;
import static com.robindrew.trading.Instruments.CHF_JPY;
import static com.robindrew.trading.Instruments.CHINA_A50;
import static com.robindrew.trading.Instruments.COPPER;
import static com.robindrew.trading.Instruments.DAX_30;
import static com.robindrew.trading.Instruments.DE_BUND_10Y;
import static com.robindrew.trading.Instruments.DOW_JONES_30;
import static com.robindrew.trading.Instruments.EURO_STOXX_50;
import static com.robindrew.trading.Instruments.EUR_AUD;
import static com.robindrew.trading.Instruments.EUR_CAD;
import static com.robindrew.trading.Instruments.EUR_CHF;
import static com.robindrew.trading.Instruments.EUR_GBP;
import static com.robindrew.trading.Instruments.EUR_JPY;
import static com.robindrew.trading.Instruments.EUR_NOK;
import static com.robindrew.trading.Instruments.EUR_NZD;
import static com.robindrew.trading.Instruments.EUR_SEK;
import static com.robindrew.trading.Instruments.EUR_TRY;
import static com.robindrew.trading.Instruments.EUR_USD;
import static com.robindrew.trading.Instruments.FTSE_100;
import static com.robindrew.trading.Instruments.GBP_AUD;
import static com.robindrew.trading.Instruments.GBP_CAD;
import static com.robindrew.trading.Instruments.GBP_CHF;
import static com.robindrew.trading.Instruments.GBP_JPY;
import static com.robindrew.trading.Instruments.GBP_NZD;
import static com.robindrew.trading.Instruments.GBP_USD;
import static com.robindrew.trading.Instruments.HANG_SENG_33;
import static com.robindrew.trading.Instruments.IBEX_35;
import static com.robindrew.trading.Instruments.NASDAQ_100;
import static com.robindrew.trading.Instruments.NATURAL_GAS;
import static com.robindrew.trading.Instruments.NIKKEI_225;
import static com.robindrew.trading.Instruments.NZD_CAD;
import static com.robindrew.trading.Instruments.NZD_CHF;
import static com.robindrew.trading.Instruments.NZD_JPY;
import static com.robindrew.trading.Instruments.NZD_USD;
import static com.robindrew.trading.Instruments.SOYBEANS;
import static com.robindrew.trading.Instruments.SP_500;
import static com.robindrew.trading.Instruments.TRY_JPY;
import static com.robindrew.trading.Instruments.USD_CAD;
import static com.robindrew.trading.Instruments.USD_CHF;
import static com.robindrew.trading.Instruments.USD_CNH;
import static com.robindrew.trading.Instruments.USD_HKD;
import static com.robindrew.trading.Instruments.USD_JPY;
import static com.robindrew.trading.Instruments.USD_MXN;
import static com.robindrew.trading.Instruments.USD_NOK;
import static com.robindrew.trading.Instruments.USD_SEK;
import static com.robindrew.trading.Instruments.USD_TRY;
import static com.robindrew.trading.Instruments.USD_ZAR;
import static com.robindrew.trading.Instruments.US_CRUDE_OIL;
import static com.robindrew.trading.Instruments.XAG_USD;
import static com.robindrew.trading.Instruments.XAU_USD;
import static com.robindrew.trading.Instruments.ZAR_JPY;

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
	public static final FxcmInstrument SPOT_AUD_JPY = new FxcmInstrument("AUD/JPY", AUD_JPY, 3);
	/** AUD/NZD. */
	public static final FxcmInstrument SPOT_AUD_NZD = new FxcmInstrument("AUD/NZD", AUD_NZD, 1);
	/** CAD/CHF. */
	public static final FxcmInstrument SPOT_CAD_CHF = new FxcmInstrument("CAD/CHF", CAD_CHF, 1);
	/** EUR/AUD. */
	public static final FxcmInstrument SPOT_EUR_AUD = new FxcmInstrument("EUR/AUD", EUR_AUD, 1);
	/** EUR/CHF. */
	public static final FxcmInstrument SPOT_EUR_CHF = new FxcmInstrument("EUR/CHF", EUR_CHF, 5);
	/** EUR/GBP. */
	public static final FxcmInstrument SPOT_EUR_GBP = new FxcmInstrument("EUR/GBP", EUR_GBP, 5);
	/** EUR/JPY. */
	public static final FxcmInstrument SPOT_EUR_JPY = new FxcmInstrument("EUR/JPY", EUR_JPY, 4);
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

	/** GBP/AUD. */
	public static final FxcmInstrument SPOT_GBP_AUD = new FxcmInstrument("GBP/AUD", GBP_AUD, 1);
	/** CAD/JPY. */
	public static final FxcmInstrument SPOT_CAD_JPY = new FxcmInstrument("CAD/JPY", CAD_JPY, 1);
	/** CHF/JPY. */
	public static final FxcmInstrument SPOT_CHF_JPY = new FxcmInstrument("CHF/JPY", CHF_JPY, 1);
	/** EUR/CAD. */
	public static final FxcmInstrument SPOT_EUR_CAD = new FxcmInstrument("EUR/CAD", EUR_CAD, 1);
	/** EUR/NOK. */
	public static final FxcmInstrument SPOT_EUR_NOK = new FxcmInstrument("EUR/NOK", EUR_NOK, 1);
	/** EUR/NZD. */
	public static final FxcmInstrument SPOT_EUR_NZD = new FxcmInstrument("EUR/NZD", EUR_NZD, 1);
	/** EUR/SEK. */
	public static final FxcmInstrument SPOT_EUR_SEK = new FxcmInstrument("EUR/SEK", EUR_SEK, 1);
	/** EUR/TRY. */
	public static final FxcmInstrument SPOT_EUR_TRY = new FxcmInstrument("EUR/TRY", EUR_TRY, 1);
	/** TRY/JPY. */
	public static final FxcmInstrument SPOT_TRY_JPY = new FxcmInstrument("TRY/JPY", TRY_JPY, 1);
	/** USD/CNH. */
	public static final FxcmInstrument SPOT_USD_CNH = new FxcmInstrument("USD/CNH", USD_CNH, 1);
	/** USD/HKD. */
	public static final FxcmInstrument SPOT_USD_HKD = new FxcmInstrument("USD/HKD", USD_HKD, 1);
	/** USD/MXN. */
	public static final FxcmInstrument SPOT_USD_MXN = new FxcmInstrument("USD/MXN", USD_MXN, 1);
	/** USD/NOK. */
	public static final FxcmInstrument SPOT_USD_NOK = new FxcmInstrument("USD/NOK", USD_NOK, 1);
	/** USD/SEK. */
	public static final FxcmInstrument SPOT_USD_SEK = new FxcmInstrument("USD/SEK", USD_SEK, 1);
	/** USD/TRY. */
	public static final FxcmInstrument SPOT_USD_TRY = new FxcmInstrument("USD/TRY", USD_TRY, 1);
	/** USD/ZAR. */
	public static final FxcmInstrument SPOT_USD_ZAR = new FxcmInstrument("USD/ZAR", USD_ZAR, 1);
	/** ZAR/JPY. */
	public static final FxcmInstrument SPOT_ZAR_JPY = new FxcmInstrument("ZAR/JPY", ZAR_JPY, 1);
	
	/** BTC USD */
	public static final FxcmInstrument SPOT_BTC_USD = new FxcmInstrument("BTC/USD", BTC_USD, 1);

	/** XAU/USD. */
	public static final FxcmInstrument SPOT_XAU_USD = new FxcmInstrument("XAU/USD", XAU_USD, 4);
	/** XAG/USD. */
	public static final FxcmInstrument SPOT_XAG_USD = new FxcmInstrument("XAG/USD", XAG_USD, 4);

	/** US30. */
	public static final FxcmInstrument SPOT_DOW_JONES = new FxcmInstrument("US30", DOW_JONES_30, 2);
	/** UK100. */
	public static final FxcmInstrument SPOT_FTSE_100 = new FxcmInstrument("UK100", FTSE_100, 2);

	/** US CRUDE. */
	public static final FxcmInstrument SPOT_US_CRUDE = new FxcmInstrument("USOil", US_CRUDE_OIL, 4);

	/** AUS200. */
	public static final FxcmInstrument AUS200 = new FxcmInstrument("AUS200", ASX_200, 1);
	/** Bund. */
	public static final FxcmInstrument BUND = new FxcmInstrument("Bund", DE_BUND_10Y, 1);
	/** CHN50. */
	public static final FxcmInstrument CHN50 = new FxcmInstrument("CHN50", CHINA_A50, 1);
	/** Copper. */
	public static final FxcmInstrument SPOT_COPPER = new FxcmInstrument("Copper", COPPER, 1);
	/** ESP35. */
	public static final FxcmInstrument ESP35 = new FxcmInstrument("ESP35", IBEX_35, 1);
	/** EUSTX50. */
	public static final FxcmInstrument EUSTX50 = new FxcmInstrument("EUSTX50", EURO_STOXX_50, 1);
	/** FRA40. */
	public static final FxcmInstrument FRA40 = new FxcmInstrument("FRA40", CAC_40, 1);
	/** GER30. */
	public static final FxcmInstrument GER30 = new FxcmInstrument("GER30", DAX_30, 1);
	/** HKG33. */
	public static final FxcmInstrument HKG33 = new FxcmInstrument("HKG33", HANG_SENG_33, 1);
	/** JPN225. */
	public static final FxcmInstrument JPN225 = new FxcmInstrument("JPN225", NIKKEI_225, 1);
	/** NAS100. */
	public static final FxcmInstrument NAS100 = new FxcmInstrument("NAS100", NASDAQ_100, 2);
	/** NGAS. */
	public static final FxcmInstrument NGAS = new FxcmInstrument("NGAS", NATURAL_GAS, 1);
	/** SOYF. */
	public static final FxcmInstrument SOYF = new FxcmInstrument("SOYF", SOYBEANS, 1);
	/** SPX500. */
	public static final FxcmInstrument SPX500 = new FxcmInstrument("SPX500", SP_500, 1);
	/** UKOil. */
	public static final FxcmInstrument UKOIL = new FxcmInstrument("UKOil", BRENT_CRUDE_OIL, 4);
	/** USDOLLAR. */
	public static final FxcmInstrument USDOLLAR = new FxcmInstrument("USDOLLAR", DOW_JONES_30, 1);
	
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
