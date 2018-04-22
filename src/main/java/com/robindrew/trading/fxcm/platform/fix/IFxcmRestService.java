package com.robindrew.trading.fxcm.platform.fix;

import com.fxcm.fix.pretrade.TradingSessionStatus;
import com.robindrew.trading.fxcm.IFxcmInstrument;
import com.robindrew.trading.fxcm.platform.fix.FxcmFixService.FxcmStreamingService;

public interface IFxcmRestService {

	void login();

	void logout();

	boolean subscribe(IFxcmInstrument instrument);

	boolean unsubscribe(IFxcmInstrument instrument);

	TradingSessionStatus getTradingSessionStatus();

	FxcmStreamingService getStreamingService();
}