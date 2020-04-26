package com.robindrew.trading.fxcm.platform.api.java;

import java.util.List;
import java.util.Set;

import com.fxcm.fix.pretrade.TradingSessionStatus;
import com.robindrew.trading.fxcm.IFxcmInstrument;
import com.robindrew.trading.fxcm.platform.IFxcmSession;
import com.robindrew.trading.fxcm.platform.api.java.command.getaccounts.FxcmTradingAccount;
import com.robindrew.trading.fxcm.platform.api.java.gateway.FxcmGateway;
import com.robindrew.trading.log.ITransactionLog;

public interface IFxcmJavaService {

	IFxcmSession getSession();

	ITransactionLog getTransactionLog();

	void login();

	void logout();

	boolean subscribe(IFxcmInstrument instrument);

	boolean unsubscribe(IFxcmInstrument instrument);

	TradingSessionStatus getTradingSessionStatus();

	Set<String> getInstrumentNames();

	Set<? extends IFxcmInstrument> getInstruments();

	List<FxcmTradingAccount> getAccounts();

	boolean isLoggedIn();

	FxcmGateway getGateway();

}