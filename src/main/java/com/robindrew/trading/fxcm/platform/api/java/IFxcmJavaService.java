package com.robindrew.trading.fxcm.platform.api.java;

import com.fxcm.fix.pretrade.TradingSessionStatus;
import com.robindrew.trading.fxcm.IFxcmInstrument;

public interface IFxcmJavaService {

	void login();

	void logout();

	boolean subscribe(IFxcmInstrument instrument);

	boolean unsubscribe(IFxcmInstrument instrument);

	TradingSessionStatus getTradingSessionStatus();

}