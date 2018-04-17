package com.robindrew.trading.fxcm.platform.rest.tradingsessionstatus;

import com.fxcm.fix.pretrade.TradingSessionStatus;
import com.robindrew.trading.fxcm.platform.rest.FxcmGateway;
import com.robindrew.trading.fxcm.platform.rest.FxcmGatewayCommand;

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
