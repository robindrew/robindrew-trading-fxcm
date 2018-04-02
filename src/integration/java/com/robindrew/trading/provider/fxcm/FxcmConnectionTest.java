package com.robindrew.trading.provider.fxcm;

import org.junit.Test;

import com.fxcm.external.api.transport.FXCMLoginProperties;
import com.fxcm.external.api.transport.GatewayFactory;
import com.fxcm.external.api.transport.IGateway;
import com.fxcm.external.api.transport.listeners.IGenericMessageListener;
import com.fxcm.external.api.transport.listeners.IStatusMessageListener;
import com.fxcm.messaging.ISessionStatus;
import com.fxcm.messaging.ITransportable;

public class FxcmConnectionTest {

	private class GenericListener implements IGenericMessageListener {

		@Override
		public void messageArrived(ITransportable message) {
			System.out.println("generic=" + message);
		}
	}

	private class StatusListener implements IStatusMessageListener {

		@Override
		public void messageArrived(ISessionStatus message) {
			System.out.println("status=" + message);
		}
	}

	@Test
	public void connect() throws Exception {
		IGateway gateway = GatewayFactory.createGateway();
		gateway.registerGenericMessageListener(new GenericListener());
		gateway.registerStatusMessageListener(new StatusListener());

		if (!gateway.isConnected()) {
			String username = null;
			String password = null;
			String station = "Demo";
			String server = "http://www.fxcorporate.com/Hosts.jsp";
			String configFile = null;
			
			FXCMLoginProperties properties = new FXCMLoginProperties(username, password, station, server, configFile);
			gateway.login(properties);
		}

		gateway.requestTradingSessionStatus();
		String accountId = gateway.requestAccounts();
		System.out.println(accountId);
	}
}
