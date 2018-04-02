package com.robindrew.trading.provider.fxcm;

import static com.robindrew.trading.Instruments.AUD_CAD;
import static com.robindrew.trading.Instruments.AUD_CHF;
import static com.robindrew.trading.Instruments.AUD_JPY;
import static com.robindrew.trading.Instruments.AUD_NZD;
import static com.robindrew.trading.Instruments.CAD_CHF;
import static com.robindrew.trading.Instruments.EUR_AUD;
import static com.robindrew.trading.Instruments.EUR_CHF;
import static com.robindrew.trading.Instruments.EUR_GBP;
import static com.robindrew.trading.Instruments.EUR_JPY;
import static com.robindrew.trading.Instruments.EUR_USD;
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

import com.robindrew.trading.IInstrument;
import com.robindrew.trading.Instrument;
import com.robindrew.trading.price.precision.IPricePrecision;
import com.robindrew.trading.price.precision.PricePrecision;

public class FxcmInstrument extends Instrument {

	/** AUD/CAD. */
	public static final FxcmInstrument AUDCAD = new FxcmInstrument("AUDCAD", AUD_CAD, 6, 40000, 190000);
	/** AUD/CHF. */
	public static final FxcmInstrument AUDCHF = new FxcmInstrument("AUDCHF", AUD_CHF, 6, 40000, 190000);
	/** AUD/JPY. */
	public static final FxcmInstrument AUDJPY = new FxcmInstrument("AUDJPY", AUD_JPY, 6, 40000, 190000);
	/** AUD/NZD. */
	public static final FxcmInstrument AUDNZD = new FxcmInstrument("AUDNZD", AUD_NZD, 6, 40000, 190000);
	/** CAD/CHF. */
	public static final FxcmInstrument CADCHF = new FxcmInstrument("CADCHF", CAD_CHF, 6, 40000, 190000);
	/** EUR/AUD. */
	public static final FxcmInstrument EURAUD = new FxcmInstrument("EURAUD", EUR_AUD, 6, 40000, 190000);
	/** EUR/CHF. */
	public static final FxcmInstrument EURCHF = new FxcmInstrument("EURCHF", EUR_CHF, 6, 40000, 190000);
	/** EUR/GBP. */
	public static final FxcmInstrument EURGBP = new FxcmInstrument("EURGBP", EUR_GBP, 6, 40000, 190000);
	/** EUR/JPY. */
	public static final FxcmInstrument EURJPY = new FxcmInstrument("EURJPY", EUR_JPY, 6, 40000, 190000);
	/** EUR/USD. */
	public static final FxcmInstrument EURUSD = new FxcmInstrument("EURUSD", EUR_USD, 6, 40000, 190000);
	/** GBP/CHF. */
	public static final FxcmInstrument GBPCHF = new FxcmInstrument("GBPCHF", GBP_CHF, 6, 40000, 190000);
	/** GBP/JPY. */
	public static final FxcmInstrument GBPJPY = new FxcmInstrument("GBPJPY", GBP_JPY, 6, 40000, 190000);
	/** GBP/NZD. */
	public static final FxcmInstrument GBPNZD = new FxcmInstrument("GBPNZD", GBP_NZD, 6, 40000, 190000);
	/** GBP/USD. */
	public static final FxcmInstrument GBPUSD = new FxcmInstrument("GBPUSD", GBP_USD, 6, 40000, 190000);
	/** NZD/CAD. */
	public static final FxcmInstrument NZDCAD = new FxcmInstrument("NZDCAD", NZD_CAD, 6, 40000, 190000);
	/** NZD/CHF. */
	public static final FxcmInstrument NZDCHF = new FxcmInstrument("NZDCHF", NZD_CHF, 6, 40000, 190000);
	/** NZD/JPY. */
	public static final FxcmInstrument NZDJPY = new FxcmInstrument("NZDJPY", NZD_JPY, 6, 40000, 190000);
	/** NZD/USD. */
	public static final FxcmInstrument NZDUSD = new FxcmInstrument("NZDUSD", NZD_USD, 6, 40000, 190000);
	/** USD/CAD. */
	public static final FxcmInstrument USDCAD = new FxcmInstrument("USDCAD", USD_CAD, 6, 40000, 190000);
	/** USD/CHF. */
	public static final FxcmInstrument USDCHF = new FxcmInstrument("USDCHF", USD_CHF, 6, 40000, 190000);
	/** USD/JPY. */
	public static final FxcmInstrument USDJPY = new FxcmInstrument("USDJPY", USD_JPY, 6, 40000, 190000);

	public static FxcmInstrument valueOf(String name) {
		switch (name) {
			case "AUDCAD":
				return AUDCAD;
			case "AUDCHF":
				return AUDCHF;
			case "AUDJPY":
				return AUDJPY;
			case "AUDNZD":
				return AUDNZD;
			case "CADCHF":
				return CADCHF;
			case "EURAUD":
				return EURAUD;
			case "EURCHF":
				return EURCHF;
			case "EURGBP":
				return EURGBP;
			case "EURJPY":
				return EURJPY;
			case "EURUSD":
				return EURUSD;
			case "GBPCHF":
				return GBPCHF;
			case "GBPJPY":
				return GBPJPY;
			case "GBPNZD":
				return GBPNZD;
			case "GBPUSD":
				return GBPUSD;
			case "NZDCAD":
				return NZDCAD;
			case "NZDCHF":
				return NZDCHF;
			case "NZDJPY":
				return NZDJPY;
			case "NZDUSD":
				return NZDUSD;
			case "USDCAD":
				return USDCAD;
			case "USDCHF":
				return USDCHF;
			case "USDJPY":
				return USDJPY;
			default:
				throw new IllegalArgumentException("Unknown instrument: '" + name + "'");
		}
	}

	private final IPricePrecision precision;

	public FxcmInstrument(String name, IInstrument underlying, int decimalPlaces, int minPrice, int maxPrice) {
		super(name, underlying);
		this.precision = new PricePrecision(decimalPlaces, minPrice, maxPrice);
	}

	public IPricePrecision getPricePrecision() {
		return precision;
	}

}
