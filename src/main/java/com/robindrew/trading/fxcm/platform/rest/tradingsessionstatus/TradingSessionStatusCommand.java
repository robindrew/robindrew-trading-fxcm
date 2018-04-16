package com.robindrew.trading.fxcm.platform.rest.tradingsessionstatus;

import com.fxcm.fix.pretrade.TradingSessionStatus;
import com.robindrew.trading.fxcm.platform.rest.FxcmGateway;
import com.robindrew.trading.fxcm.platform.rest.FxcmGatewayCommand;

public class TradingSessionStatusCommand extends FxcmGatewayCommand<TradingSessionStatus> {

	@Override
	protected TradingSessionStatus executeCommand(FxcmGateway gateway) {
		String requestId = gateway.getGateway().requestTradingSessionStatus();
		return gateway.getResponseCache().getAndClose(requestId);
	}

}
