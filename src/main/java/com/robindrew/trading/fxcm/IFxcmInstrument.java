package com.robindrew.trading.fxcm;

import com.robindrew.trading.provider.ITradeDataProviderInstrument;

public interface IFxcmInstrument extends ITradeDataProviderInstrument {

	String getSymbol();
}
