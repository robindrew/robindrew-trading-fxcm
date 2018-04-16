package com.robindrew.trading.fxcm.platform.rest.logout;

import com.robindrew.trading.fxcm.platform.rest.FxcmGateway;
import com.robindrew.trading.fxcm.platform.rest.FxcmGatewayCommand;

public class LogoutCommand extends FxcmGatewayCommand<Boolean> {

	@Override
	protected Boolean executeCommand(FxcmGateway gateway) {
		gateway.getGateway().logout();
		return true;
	}

}
