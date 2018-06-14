package com.robindrew.trading.fxcm.platform;

import com.robindrew.trading.fxcm.IFxcmInstrument;
import com.robindrew.trading.fxcm.platform.api.java.FxcmJavaService;
import com.robindrew.trading.fxcm.platform.api.java.IFxcmJavaService;
import com.robindrew.trading.fxcm.platform.api.java.position.FxcmPositionService;
import com.robindrew.trading.fxcm.platform.api.java.streaming.FxcmStreamingService;
import com.robindrew.trading.fxcm.platform.api.java.streaming.IFxcmStreamingService;
import com.robindrew.trading.platform.TradingPlatform;

public class FxcmTradingPlatform extends TradingPlatform<IFxcmInstrument> implements IFxcmTradingPlatform {

	private final FxcmJavaService javaService;
	private final FxcmPositionService position;
	private final FxcmStreamingService streaming;

	public FxcmTradingPlatform(FxcmJavaService javaService) {
		this.javaService = javaService;
		this.position = new FxcmPositionService(javaService);
		this.streaming = new FxcmStreamingService(javaService);
	}

	@Override
	public FxcmPositionService getPositionService() {
		return position;
	}

	@Override
	public IFxcmStreamingService getStreamingService() {
		return streaming;
	}

	@Override
	public IFxcmJavaService getJavaService() {
		return javaService;
	}

	@Override
	public IFxcmSession getSession() {
		return getJavaService().getSession();
	}

}
