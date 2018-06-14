package com.robindrew.trading.fxcm.platform;

import com.robindrew.trading.fxcm.IFxcmInstrument;
import com.robindrew.trading.fxcm.platform.api.java.IFxcmJavaService;
import com.robindrew.trading.fxcm.platform.api.java.streaming.IFxcmStreamingService;
import com.robindrew.trading.platform.ITradingPlatform;

public interface IFxcmTradingPlatform extends ITradingPlatform<IFxcmInstrument> {

	IFxcmSession getSession();

	IFxcmJavaService getJavaService();

	@Override
	IFxcmStreamingService getStreamingService();

}
