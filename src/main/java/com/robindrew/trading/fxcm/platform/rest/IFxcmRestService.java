package com.robindrew.trading.fxcm.platform.rest;

import com.fxcm.fix.pretrade.TradingSessionStatus;
import com.robindrew.trading.fxcm.FxcmInstrument;
import com.robindrew.trading.platform.streaming.IStreamingService;

public interface IFxcmRestService {

	void login();

	void logout();

	boolean subscribe(FxcmInstrument instrument);

	TradingSessionStatus getTradingSessionStatus();

	IStreamingService getStreamingService();
}