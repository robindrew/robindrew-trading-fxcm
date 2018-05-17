package com.robindrew.trading.fxcm.platform.api.java.logout;

import com.robindrew.trading.fxcm.platform.api.java.FxcmGateway;
import com.robindrew.trading.fxcm.platform.api.java.FxcmGatewayCommand;

public class LogoutCommand extends FxcmGatewayCommand<Boolean> {

	@Override
	protected Boolean executeCommand(FxcmGateway gateway) {
		gateway.getGateway().logout();
		return true;
	}

}
