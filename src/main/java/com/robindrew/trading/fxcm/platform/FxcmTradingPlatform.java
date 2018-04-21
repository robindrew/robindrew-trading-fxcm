package com.robindrew.trading.fxcm.platform;

import com.robindrew.trading.fxcm.platform.fix.FxcmPositionService;
import com.robindrew.trading.fxcm.platform.fix.FxcmFixService;
import com.robindrew.trading.platform.TradingPlatform;

public class FxcmTradingPlatform extends TradingPlatform {

	private final FxcmPositionService position;

	public FxcmTradingPlatform(FxcmFixService fix) {
		this.position = new FxcmPositionService(fix);
	}

	@Override
	public FxcmPositionService getPositionService() {
		return position;
	}

}
