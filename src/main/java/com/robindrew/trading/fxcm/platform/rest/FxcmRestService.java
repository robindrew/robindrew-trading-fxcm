package com.robindrew.trading.fxcm.platform.rest;

import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fxcm.external.api.transport.FXCMLoginProperties;
import com.fxcm.external.api.transport.GatewayFactory;
import com.fxcm.external.api.transport.IGateway;
import com.fxcm.external.api.transport.listeners.IGenericMessageListener;
import com.fxcm.external.api.transport.listeners.IStatusMessageListener;
import com.fxcm.fix.SubscriptionRequestTypeFactory;
import com.fxcm.fix.TradingSecurity;
import com.fxcm.fix.pretrade.MarketDataRequest;
import com.fxcm.fix.pretrade.MarketDataSnapshot;
import com.fxcm.fix.pretrade.TradingSessionStatus;
import com.fxcm.messaging.ISessionStatus;
import com.fxcm.messaging.ITransportable;
import com.google.common.base.Stopwatch;
import com.robindrew.common.util.Check;
import com.robindrew.common.util.Java;
import com.robindrew.trading.fxcm.platform.IFxcmSession;

public class FxcmRestService implements IFxcmRestService {

	public static void main(String[] args) {
		System.out.println(System.currentTimeMillis());
	}

	private static final Logger log = LoggerFactory.getLogger(FxcmRestService.class);

	private final IFxcmSession session;
	private final IGateway gateway;
	private final TransportableCache cache = new TransportableCache();
	private final ResponseListener responseListener = new ResponseListener();
	private final StatusListener statusListener = new StatusListener();

	public FxcmRestService(IFxcmSession session) {
		this.session = Check.notNull("session", session);
		this.gateway = GatewayFactory.createGateway();

		// Initialise gateway
		gateway.registerGenericMessageListener(responseListener);
		gateway.registerStatusMessageListener(statusListener);
	}

	@Override
	public void login() {
		try {

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
			log.info("Logging in ...");
			Stopwatch timer = Stopwatch.createStarted();
			gateway.login(properties);
			timer.stop();
			log.info("Logged in ({})", timer);

		} catch (Exception e) {
			throw Java.propagate(e);
		}
	}

	public void logout() {
		gateway.logout();
	}

	public Future<TradingSessionStatus> getTradingSessionStatus() {
		String requestId = gateway.requestTradingSessionStatus();
		return cache.get(requestId);
	}

	@SuppressWarnings("unchecked")
	public Future<MarketDataSnapshot> getMarketDataSnapshot(TradingSessionStatus status) {
		try {

			MarketDataRequest marketData = new MarketDataRequest();
			Enumeration<TradingSecurity> securities = status.getSecurities();
			while (securities.hasMoreElements()) {
				TradingSecurity symbol = securities.nextElement();
				marketData.addRelatedSymbol(symbol);
				break;
			}
			marketData.setSubscriptionRequestType(SubscriptionRequestTypeFactory.SNAPSHOT);
			marketData.setMDEntryTypeSet(MarketDataRequest.MDENTRYTYPESET_TICKALL);
			String requestId = gateway.sendMessage(marketData);

			return cache.get(requestId);

		} catch (Exception e) {
			throw Java.propagate(e);
		}
	}

	private class ResponseListener implements IGenericMessageListener {

		private final Logger log = LoggerFactory.getLogger(ResponseListener.class);

		@Override
		public void messageArrived(ITransportable message) {
			try {

				// Is this message a request/response
				String requestId = message.getRequestID();
				if (requestId != null) {
					log.debug("messageArrived(requestId={})", requestId);
					cache.put(requestId, message);
					return;
				}

				// Handle snapshots (ticking prices)
				if (message instanceof MarketDataSnapshot) {
					MarketDataSnapshot snapshot = (MarketDataSnapshot) message;

					for (Field field : MarketDataSnapshot.class.getDeclaredFields()) {
						field.setAccessible(true);
						System.out.println(field.getName() + " = " + field.get(snapshot));
					}
					System.out.println();
					return;
				}

				log.warn("Message not handled: " + message);

			} catch (Exception e) {
				throw Java.propagate(e);
			}
		}
	}

	private class StatusListener implements IStatusMessageListener {

		private final Logger log = LoggerFactory.getLogger(FxcmRestService.StatusListener.class);

		@Override
		public void messageArrived(ISessionStatus message) {
			log.debug("Status Message: ({}) '{}'", message.getStatusCode(), message.getStatusMessage());
		}
	}

}
