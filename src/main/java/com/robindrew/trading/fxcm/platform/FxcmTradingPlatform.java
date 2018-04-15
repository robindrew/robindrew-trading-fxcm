package com.robindrew.trading.fxcm.platform;

import com.robindrew.trading.fxcm.platform.rest.FxcmPositionService;
import com.robindrew.trading.fxcm.platform.rest.FxcmRestService;
import com.robindrew.trading.platform.TradingPlatform;

public class FxcmTradingPlatform extends TradingPlatform {

	private final FxcmPositionService position;

	public FxcmTradingPlatform(FxcmRestService service) {
		this.position = new FxcmPositionService(service);
	}

	@Override
	public FxcmPositionService getPositionService() {
		return position;
	}

}
