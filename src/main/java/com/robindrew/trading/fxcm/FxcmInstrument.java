package com.robindrew.trading.fxcm;

import static com.robindrew.trading.Instruments.AUD_CAD;
import static com.robindrew.trading.Instruments.AUD_CHF;
import static com.robindrew.trading.Instruments.AUD_JPY;
import static com.robindrew.trading.Instruments.AUD_NZD;
import static com.robindrew.trading.Instruments.AUD_USD;
import static com.robindrew.trading.Instruments.CAD_CHF;
import static com.robindrew.trading.Instruments.DOW_JONES;
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

import com.robindrew.trading.IInstrument;
import com.robindrew.trading.Instrument;
import com.robindrew.trading.Instruments;
import com.robindrew.trading.price.precision.IPricePrecision;
import com.robindrew.trading.price.precision.PricePrecision;

public class FxcmInstrument extends Instrument {

	/** AUD/USD. */
	public static final FxcmInstrument SPOT_AUD_USD = new FxcmInstrument("AUD/USD", AUD_USD, 6, 40000, 190000);
	/** AUD/CAD. */
	public static final FxcmInstrument SPOT_AUD_CAD = new FxcmInstrument("AUD/CAD", AUD_CAD, 1, 40000, 190000);
	/** AUD/CHF. */
	public static final FxcmInstrument SPOT_AUD_CHF = new FxcmInstrument("AUD/CHF", AUD_CHF, 1, 40000, 190000);
	/** AUD/JPY. */
	public static final FxcmInstrument SPOT_AUD_JPY = new FxcmInstrument("AUD/JPY", AUD_JPY, 1, 40000, 190000);
	/** AUD/NZD. */
	public static final FxcmInstrument SPOT_AUD_NZD = new FxcmInstrument("AUD/NZD", AUD_NZD, 1, 40000, 190000);
	/** CAD/CHF. */
	public static final FxcmInstrument SPOT_CAD_CHF = new FxcmInstrument("CAD/CHF", CAD_CHF, 1, 40000, 190000);
	/** EUR/AUD. */
	public static final FxcmInstrument SPOT_EUR_AUD = new FxcmInstrument("EUR/AUD", EUR_AUD, 1, 40000, 190000);
	/** EUR/CHF. */
	public static final FxcmInstrument SPOT_EUR_CHF = new FxcmInstrument("EUR/CHF", EUR_CHF, 1, 40000, 190000);
	/** EUR/GBP. */
	public static final FxcmInstrument SPOT_EUR_GBP = new FxcmInstrument("EUR/GBP", EUR_GBP, 1, 40000, 190000);
	/** EUR/JPY. */
	public static final FxcmInstrument SPOT_EUR_JPY = new FxcmInstrument("EUR/JPY", EUR_JPY, 1, 40000, 190000);
	/** EUR/USD. */
	public static final FxcmInstrument SPOT_EUR_USD = new FxcmInstrument("EUR/USD", EUR_USD, 6, 40000, 190000);
	/** GBP/CAD. */
	public static final FxcmInstrument SPOT_GBP_CAD = new FxcmInstrument("GBP/CAD", GBP_CAD, 6, 40000, 190000);
	/** GBP/CHF. */
	public static final FxcmInstrument SPOT_GBP_CHF = new FxcmInstrument("GBP/CHF", GBP_CHF, 1, 40000, 190000);
	/** GBP/JPY. */
	public static final FxcmInstrument SPOT_GBP_JPY = new FxcmInstrument("GBP/JPY", GBP_JPY, 4, 40000, 190000);
	/** GBP/NZD. */
	public static final FxcmInstrument SPOT_GBP_NZD = new FxcmInstrument("GBP/NZD", GBP_NZD, 1, 40000, 190000);
	/** GBP/USD. */
	public static final FxcmInstrument SPOT_GBP_USD = new FxcmInstrument("GBP/USD", GBP_USD, 6, 40000, 190000);
	/** NZD/CAD. */
	public static final FxcmInstrument SPOT_NZD_CAD = new FxcmInstrument("NZD/CAD", NZD_CAD, 1, 40000, 190000);
	/** NZD/CHF. */
	public static final FxcmInstrument SPOT_NZD_CHF = new FxcmInstrument("NZD/CHF", NZD_CHF, 1, 40000, 190000);
	/** NZD/JPY. */
	public static final FxcmInstrument SPOT_NZD_JPY = new FxcmInstrument("NZD/JPY", NZD_JPY, 1, 40000, 190000);
	/** NZD/USD. */
	public static final FxcmInstrument SPOT_NZD_USD = new FxcmInstrument("NZD/USD", NZD_USD, 1, 40000, 190000);
	/** USD/CAD. */
	public static final FxcmInstrument SPOT_USD_CAD = new FxcmInstrument("USD/CAD", USD_CAD, 6, 40000, 190000);
	/** USD/CHF. */
	public static final FxcmInstrument SPOT_USD_CHF = new FxcmInstrument("USD/CHF", USD_CHF, 1, 40000, 190000);
	/** USD/JPY. */
	public static final FxcmInstrument SPOT_USD_JPY = new FxcmInstrument("USD/JPY", USD_JPY, 4, 40000, 190000);

	/** XAU/USD. */
	public static final FxcmInstrument SPOT_GOLD = new FxcmInstrument("XAU/USD", Instruments.GOLD, 3, 40000, 190000);
	/** XAU/USD. */
	public static final FxcmInstrument SPOT_SILVER = new FxcmInstrument("XAG/USD", Instruments.GOLD, 3, 40000, 190000);

	/** US30. */
	public static final FxcmInstrument SPOT_DOW_JONES = new FxcmInstrument("US30", DOW_JONES, 2, 40000, 190000);
	/** UK100. */
	public static final FxcmInstrument SPOT_FTSE_100 = new FxcmInstrument("UK100", FTSE_100, 2, 40000, 190000);

	/** US CRUDE. */
	public static final FxcmInstrument SPOT_US_CRUDE = new FxcmInstrument("USOil", US_CRUDE_OIL, 4, 40000, 190000);

	public static FxcmInstrument valueOf(String name) {
		switch (name) {
			case "AUD/USD":
				return SPOT_AUD_USD;
			case "AUD/CAD":
				return SPOT_AUD_CAD;
			case "AUD/CHF":
				return SPOT_AUD_CHF;
			case "AUD/JPY":
				return SPOT_AUD_JPY;
			case "AUD/NZD":
				return SPOT_AUD_NZD;
			case "CAD/CHF":
				return SPOT_CAD_CHF;
			case "EUR/AUD":
				return SPOT_EUR_AUD;
			case "EUR/CHF":
				return SPOT_EUR_CHF;
			case "EUR/GBP":
				return SPOT_EUR_GBP;
			case "EUR/JPY":
				return SPOT_EUR_JPY;
			case "EUR/USD":
				return SPOT_EUR_USD;
			case "GBP/CAD":
				return SPOT_GBP_CAD;
			case "GBP/CHF":
				return SPOT_GBP_CHF;
			case "GBP/JPY":
				return SPOT_GBP_JPY;
			case "GBP/NZD":
				return SPOT_GBP_NZD;
			case "GBP/USD":
				return SPOT_GBP_USD;
			case "NZD/CAD":
				return SPOT_NZD_CAD;
			case "NZD/CHF":
				return SPOT_NZD_CHF;
			case "NZD/JPY":
				return SPOT_NZD_JPY;
			case "NZD/USD":
				return SPOT_NZD_USD;
			case "USD/CAD":
				return SPOT_USD_CAD;
			case "USD/CHF":
				return SPOT_USD_CHF;
			case "USD/JPY":
				return SPOT_USD_JPY;
			case "XAU/USD":
				return SPOT_GOLD;
			case "XAG/USD":
				return SPOT_SILVER;
			case "UK100":
				return SPOT_FTSE_100;
			case "US30":
				return SPOT_DOW_JONES;
			case "USOil":
				return SPOT_US_CRUDE;
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
