package com.robindrew.trading.fxcm;

import com.robindrew.trading.provider.ITradingProviderInstrument;

public interface IFxcmInstrument extends ITradingProviderInstrument {

	String getSymbol();
}
