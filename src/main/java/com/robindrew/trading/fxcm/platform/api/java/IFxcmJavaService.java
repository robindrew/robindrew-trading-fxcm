package com.robindrew.trading.fxcm.platform.api.java;

import com.fxcm.fix.pretrade.TradingSessionStatus;
import com.robindrew.trading.fxcm.IFxcmInstrument;
import com.robindrew.trading.fxcm.platform.IFxcmSession;
import com.robindrew.trading.log.ITransactionLog;

public interface IFxcmJavaService {

	IFxcmSession getSession();

	ITransactionLog getTransactionLog();

	void login();

	void logout();

	boolean subscribe(IFxcmInstrument instrument);

	boolean unsubscribe(IFxcmInstrument instrument);

	TradingSessionStatus getTradingSessionStatus();

}