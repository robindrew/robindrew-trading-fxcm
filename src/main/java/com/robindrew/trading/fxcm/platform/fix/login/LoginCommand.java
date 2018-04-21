package com.robindrew.trading.fxcm.platform.fix.login;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fxcm.external.api.transport.FXCMLoginProperties;
import com.robindrew.trading.fxcm.platform.IFxcmSession;
import com.robindrew.trading.fxcm.platform.fix.FxcmGateway;
import com.robindrew.trading.fxcm.platform.fix.FxcmGatewayCommand;

public class LoginCommand extends FxcmGatewayCommand<Boolean> {

	private static final Logger log = LoggerFactory.getLogger(LoginCommand.class);

	private final IFxcmSession session;

	public LoginCommand(IFxcmSession session) {
		this.session = session;
	}

	@Override
	public Boolean executeCommand(FxcmGateway gateway) throws Exception {

		String username = session.getCredentials().getUsername();
		String password = session.getCredentials().getPassword();
		String station = session.getEnvironment().getStation();
		String server = session.getEnvironment().getServer();
		String configFile = null;

		FXCMLoginProperties properties = new FXCMLoginProperties(username, password, station, server, configFile);

		log.info("[User] {}", username);
		log.info("[Station] {}", station);
		log.info("[Server] {}", server);

		// Login!
		gateway.getGateway().login(properties);

		// Finished!
		return true;
	}

}
