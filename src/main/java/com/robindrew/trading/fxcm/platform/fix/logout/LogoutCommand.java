package com.robindrew.trading.fxcm.platform.fix.logout;

import com.robindrew.trading.fxcm.platform.fix.FxcmGateway;
import com.robindrew.trading.fxcm.platform.fix.FxcmGatewayCommand;

public class LogoutCommand extends FxcmGatewayCommand<Boolean> {

	@Override
	protected Boolean executeCommand(FxcmGateway gateway) {
		gateway.getGateway().logout();
		return true;
	}

}
