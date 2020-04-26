package com.robindrew.trading.fxcm;

import com.robindrew.trading.provider.ITradingInstrument;

public interface IFxcmInstrument extends ITradingInstrument {

	String getSymbol();
}
