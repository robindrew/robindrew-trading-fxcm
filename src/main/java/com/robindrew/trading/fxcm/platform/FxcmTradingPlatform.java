package com.robindrew.trading.fxcm.platform;

import com.robindrew.trading.fxcm.IFxcmInstrument;
import com.robindrew.trading.fxcm.platform.api.java.FxcmJavaService;
import com.robindrew.trading.fxcm.platform.api.java.IFxcmJavaService;
import com.robindrew.trading.fxcm.platform.api.java.account.FxcmAccountService;
import com.robindrew.trading.fxcm.platform.api.java.position.FxcmPositionService;
import com.robindrew.trading.fxcm.platform.api.java.streaming.FxcmStreamingService;
import com.robindrew.trading.fxcm.platform.api.java.streaming.IFxcmStreamingService;
import com.robindrew.trading.platform.TradingPlatform;

public class FxcmTradingPlatform extends TradingPlatform<IFxcmInstrument> implements IFxcmTradingPlatform {

	private final FxcmJavaService java;
	private final FxcmAccountService account;
	private final FxcmPositionService position;
	private final FxcmStreamingService streaming;

	public FxcmTradingPlatform(FxcmJavaService java) {
		this.java = java;
		this.account = new FxcmAccountService(java);
		this.position = new FxcmPositionService(java);
		this.streaming = new FxcmStreamingService(java);
	}

	@Override
	public FxcmAccountService getAccountService() {
		return account;
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
		return java;
	}

	@Override
	public IFxcmSession getSession() {
		return getJavaService().getSession();
	}

}
