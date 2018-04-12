package com.robindrew.trading.fxcm.platform.rest;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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
import com.robindrew.common.util.Threads;
import com.robindrew.trading.fxcm.platform.IFxcmSession;

public class FxcmRestService implements IFxcmRestService {

	private static final Logger log = LoggerFactory.getLogger(FxcmRestService.class);

	private final IFxcmSession session;
	private final IGateway gateway;
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
			log.info("Logging in ...");
			Stopwatch timer = Stopwatch.createStarted();
			gateway.login(properties);
			timer.stop();
			log.info("Logged in ({})", timer);
			log.info("[User] {}", username);
			log.info("[Station] {}", station);
			log.info("[Server] {}", server);

		} catch (Exception e) {
			throw Java.propagate(e);
		}
	}

	public void logout() {
		gateway.logout();
	}

	public TradingSessionStatus getTradingSessionStatus() {
		String requestId = gateway.requestTradingSessionStatus();
		return (TradingSessionStatus) responseListener.getResponse(requestId);
	}

	@SuppressWarnings("unchecked")
	public MarketDataSnapshot getMarketDataSnapshot(TradingSessionStatus status) {
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

			return (MarketDataSnapshot) responseListener.getResponse(requestId);

		} catch (Exception e) {
			throw Java.propagate(e);
		}
	}

	private class ResponseListener implements IGenericMessageListener {

		private final ConcurrentMap<String, ITransportable> responseMap = new ConcurrentHashMap<>();

		@Override
		public void messageArrived(ITransportable message) {
			try {
				// log.info("RequestID: {}", message.getRequestID());
				// log.info("TradingSessionID: {}", message.getTradingSessionID());
				// log.info("TradingSessionSubID: {}", message.getTradingSessionSubID());
				// log.info("Type: {}", message.getType());
				// log.info("Message: {}", message);

				if (message instanceof MarketDataSnapshot) {
					MarketDataSnapshot snapshot = (MarketDataSnapshot) message;
					String symbol = snapshot.getInstrument().getSymbol();
					if (symbol.equals("EUR/USD")) {
						log.info(symbol + ", bid=" + snapshot.getBidClose() + ", ask=" + snapshot.getAskClose());
					}
				}

				String requestId = message.getRequestID();
				if (requestId != null) {
					responseMap.put(requestId, message);
				}
			} catch (Exception e) {
				throw Java.propagate(e);
			}
		}

		public ITransportable getResponse(String requestId) {
			while (true) {
				ITransportable response = responseMap.get(requestId);
				if (response != null) {
					return response;
				}
				Threads.sleep(50);
			}
		}
	}

	private class StatusListener implements IStatusMessageListener {

		@Override
		public void messageArrived(ISessionStatus message) {
			log.info("Status Message: ({}) '{}'", message.getStatusCode(), message.getStatusMessage());
		}
	}

}
