package com.robindrew.trading.fxcm.platform.api.java.command.tradingsessionstatus;

import com.fxcm.fix.pretrade.TradingSessionStatus;
import com.robindrew.trading.fxcm.platform.api.java.command.FxcmGatewayCommand;
import com.robindrew.trading.fxcm.platform.api.java.gateway.FxcmGateway;

public class TradingSessionStatusCommand extends FxcmGatewayCommand<TradingSessionStatus> {

	@Override
	protected TradingSessionStatus executeCommand(FxcmGateway gateway) {
		// return gateway.execute(new TradingSessionStatusRequest());

		// We have to call requestTradingSessionStatus() as it sets an internal flag
		// FXCMGateway.mTradingSessionRetrieved = true
		String requestId = gateway.getGateway().requestTradingSessionStatus();
		return gateway.getResponseCache().getAndClose(requestId);
	}

}
