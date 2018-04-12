package com.robindrew.trading.fxcm;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fxcm.external.api.transport.FXCMLoginProperties;
import com.fxcm.external.api.transport.GatewayFactory;
import com.fxcm.external.api.transport.IGateway;
import com.fxcm.external.api.transport.listeners.IGenericMessageListener;
import com.fxcm.external.api.transport.listeners.IStatusMessageListener;
import com.fxcm.messaging.ISessionStatus;
import com.fxcm.messaging.ITransportable;

public class FxcmConnectionTest {

	private static final Logger log = LoggerFactory.getLogger(FxcmConnectionTest.class);
	
	private class GenericListener implements IGenericMessageListener {

		@Override
		public void messageArrived(ITransportable message) {
			log.info("ITransportable = " + message + ")");
		}
	}

	private class StatusListener implements IStatusMessageListener {

		@Override
		public void messageArrived(ISessionStatus message) {
			log.info("messageArrived(" + message.getStatusMessage() + ")");
		}
	}

	@Test
	public void connect() throws Exception {
		IGateway gateway = GatewayFactory.createGateway();
		gateway.registerGenericMessageListener(new GenericListener());
		gateway.registerStatusMessageListener(new StatusListener());

		if (!gateway.isConnected()) {
			String username = "";
			String password = "";
			String station = "Demo";
			String server = "http://www.fxcorporate.com/Hosts.jsp";
			String configFile = null;

			FXCMLoginProperties properties = new FXCMLoginProperties(username, password, station, server, configFile);
			log.info("LOGIN");
			gateway.login(properties);
			log.info("LOGGEDIN");
		}

		log.info("requestTradingSessionStatus()");
		String status = gateway.requestTradingSessionStatus();
		log.info("requestTradingSessionStatus() -> " + status);

		log.info("requestAccounts()");
		String accountId = gateway.requestAccounts();
		log.info("requestAccounts() -> " + accountId);
	}
}
