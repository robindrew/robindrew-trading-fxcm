package com.robindrew.trading.fxcm.platform.api.java.command.logout;

import com.robindrew.trading.fxcm.platform.api.java.command.FxcmGatewayCommand;
import com.robindrew.trading.fxcm.platform.api.java.gateway.FxcmGateway;

public class LogoutCommand extends FxcmGatewayCommand<Boolean> {

	@Override
	protected Boolean executeCommand(FxcmGateway gateway) {
		gateway.getGateway().logout();
		return true;
	}

}
