package com.robindrew.trading.fxcm.platform;

import com.robindrew.trading.fxcm.IFxcmInstrument;
import com.robindrew.trading.fxcm.platform.api.java.FxcmJavaService;
import com.robindrew.trading.fxcm.platform.api.java.FxcmPositionService;
import com.robindrew.trading.fxcm.platform.api.java.IFxcmStreamingService;
import com.robindrew.trading.platform.TradingPlatform;

public class FxcmTradingPlatform extends TradingPlatform<IFxcmInstrument> implements IFxcmTradingPlatform {

	private final FxcmPositionService position;

	public FxcmTradingPlatform(FxcmJavaService java) {
		this.position = new FxcmPositionService(java);
	}

	@Override
	public FxcmPositionService getPositionService() {
		return position;
	}

	@Override
	public IFxcmStreamingService getStreamingService() {
		return (IFxcmStreamingService) super.getStreamingService();
	}

}
