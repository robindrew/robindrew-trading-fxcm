package com.robindrew.trading.fxcm.platform.rest;

import static com.fxcm.external.api.util.MessageGenerator.generateCloseMarketOrder;
import static com.robindrew.trading.fxcm.platform.rest.FxcmRest.toFxcmInstrument;
import static com.robindrew.trading.fxcm.platform.rest.FxcmRest.toPriceCandle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fxcm.external.api.transport.FXCMLoginProperties;
import com.fxcm.external.api.transport.GatewayFactory;
import com.fxcm.external.api.transport.IGateway;
import com.fxcm.external.api.transport.listeners.IGenericMessageListener;
import com.fxcm.external.api.transport.listeners.IStatusMessageListener;
import com.fxcm.fix.ISide;
import com.fxcm.fix.Instrument;
import com.fxcm.fix.SubscriptionRequestTypeFactory;
import com.fxcm.fix.TradingSecurity;
import com.fxcm.fix.posttrade.CollateralReport;
import com.fxcm.fix.posttrade.PositionReport;
import com.fxcm.fix.pretrade.MarketDataRequest;
import com.fxcm.fix.pretrade.MarketDataSnapshot;
import com.fxcm.fix.pretrade.TradingSessionStatus;
import com.fxcm.fix.trade.OrderSingle;
import com.fxcm.messaging.ISessionStatus;
import com.fxcm.messaging.ITransportable;
import com.google.common.base.Stopwatch;
import com.robindrew.common.util.Check;
import com.robindrew.common.util.Java;
import com.robindrew.trading.IInstrument;
import com.robindrew.trading.fxcm.FxcmInstrument;
import com.robindrew.trading.fxcm.platform.IFxcmSession;
import com.robindrew.trading.fxcm.platform.rest.getaccounts.FxcmTradingAccount;
import com.robindrew.trading.fxcm.platform.rest.getaccounts.GetAccountsResponse;
import com.robindrew.trading.fxcm.platform.rest.getopenpositions.FxcmPosition;
import com.robindrew.trading.fxcm.platform.rest.getopenpositions.GetOpenPositionsResponse;
import com.robindrew.trading.fxcm.platform.rest.response.GatewayResponseCache;
import com.robindrew.trading.fxcm.platform.rest.response.GatewayResponseCache.GatewayResponse;
import com.robindrew.trading.platform.TradingPlatformException;
import com.robindrew.trading.platform.streaming.IInstrumentPriceStream;
import com.robindrew.trading.platform.streaming.IStreamingService;
import com.robindrew.trading.platform.streaming.StreamingService;
import com.robindrew.trading.price.candle.IPriceCandle;

public class FxcmRestService implements IFxcmRestService {

	private static final Logger log = LoggerFactory.getLogger(FxcmRestService.class);

	private final IFxcmSession session;
	private final IGateway gateway;
	private final GatewayResponseCache gatewayResponses = new GatewayResponseCache();
	private final ResponseListener responseListener = new ResponseListener();
	private final StatusListener statusListener = new StatusListener();
	private final FxcmStreamingService streaming = new FxcmStreamingService();

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
			log.info("login()");
			Stopwatch timer = Stopwatch.createStarted();
			gateway.login(properties);
			timer.stop();
			log.info("login() took {}", timer);

			// We are required to get the session status immediately after login
			getTradingSessionStatus();

		} catch (Exception e) {
			throw new TradingPlatformException("login() failed", e);
		}
	}

	@Override
	public void logout() {
		gateway.logout();
	}

	public void closePosition(FxcmPosition position) {
		try {
			String id = position.getId();
			String accountId = position.getAccount();
			double quantity = position.getTradeSize().doubleValue();
			ISide side = FxcmRest.toSide(position.getDirection().invert());
			String symbol = position.getInstrument().getName();
			OrderSingle close = generateCloseMarketOrder(id, accountId, quantity, side, symbol, "Completely close position");

			log.info("closePosition()");
			Stopwatch timer = Stopwatch.createStarted();

			String requestId = gateway.sendMessage(close);
			ITransportable response = gatewayResponses.getAndClose(requestId);
			System.out.println(response);

			timer.stop();
			log.info("closePosition() took {}", timer);

		} catch (Exception e) {
			throw Java.propagate(e);
		}
	}

	public List<FxcmTradingAccount> getAccounts() {
		log.info("getAccounts()");
		Stopwatch timer = Stopwatch.createStarted();

		String requestId = gateway.requestAccounts();
		try (GatewayResponse response = gatewayResponses.get(requestId)) {

			// Wait for the accounts
			GetAccountsResponse accounts = response.populate(new GetAccountsResponse());

			List<FxcmTradingAccount> list = new ArrayList<>();
			for (CollateralReport report : accounts.getReportList()) {
				FxcmTradingAccount account = new FxcmTradingAccount(report);
				log.info("{}", account);
				list.add(account);
			}

			timer.stop();
			log.info("getAccounts() took {}", timer);
			return list;

		} catch (Exception e) {
			throw new TradingPlatformException("getPositions() failed", e);
		}
	}

	public List<FxcmPosition> getPositions() {
		log.info("getPositions()");
		Stopwatch timer = Stopwatch.createStarted();

		String requestId = gateway.requestOpenPositions();
		try (GatewayResponse response = gatewayResponses.get(requestId)) {

			// Wait for the positions
			GetOpenPositionsResponse positions = response.populate(new GetOpenPositionsResponse());

			List<FxcmPosition> list = new ArrayList<>();
			for (PositionReport report : positions.getReportList()) {
				FxcmPosition position = new FxcmPosition(report);
				log.info("{}", position);
				list.add(position);
			}

			timer.stop();
			log.info("getPositions() took {}", timer);
			return list;

		} catch (Exception e) {
			throw new TradingPlatformException("getPositions() failed", e);
		}
	}

	@Override
	public TradingSessionStatus getTradingSessionStatus() {
		log.info("getTradingSessionStatus()");
		Stopwatch timer = Stopwatch.createStarted();

		String requestId = gateway.requestTradingSessionStatus();
		TradingSessionStatus status = gatewayResponses.getAndClose(requestId);

		timer.stop();
		log.info("getTradingSessionStatus() took {}", timer);
		return status;
	}

	public Future<MarketDataSnapshot> getMarketDataSnapshot(FxcmInstrument instrument) {
		try {

			TradingSessionStatus status = getTradingSessionStatus();

			MarketDataRequest marketData = new MarketDataRequest();
			marketData.addRelatedSymbol(getInstrument(status, instrument));
			marketData.setSubscriptionRequestType(SubscriptionRequestTypeFactory.SNAPSHOT);
			marketData.setMDEntryTypeSet(MarketDataRequest.MDENTRYTYPESET_TICKALL);

			// Request / Response
			String requestId = gateway.sendMessage(marketData);
			return gatewayResponses.getAndClose(requestId);

		} catch (Exception e) {
			throw Java.propagate(e);
		}
	}

	public void handleMessage(ITransportable message) {
		try {
			String requestId = message.getRequestID();

			// Is this message a request/response
			if (requestId != null) {
				log.info("messageArrived(requestId={}, type={})", requestId, message.getClass().getName());
				gatewayResponses.put(requestId, message);
				return;
			}

			// Handle snapshots (ticking prices)
			if (message instanceof MarketDataSnapshot) {
				handleMarketDataSnapshot((MarketDataSnapshot) message);
				return;
			}

			log.warn("Message not handled: " + message);

		} catch (Exception e) {
			throw Java.propagate(e);
		}
	}

	private void handleMarketDataSnapshot(MarketDataSnapshot snapshot) throws Exception {
		FxcmInstrument instrument = toFxcmInstrument(snapshot.getInstrument());
		IPriceCandle candle = toPriceCandle(snapshot);
		handlePriceTick(instrument, candle);
	}

	private void handlePriceTick(FxcmInstrument instrument, IPriceCandle candle) {
		log.debug("[{}] {}", instrument, candle);
	}

	private Instrument getInstrument(TradingSessionStatus status, FxcmInstrument instrument) {
		try {

			Set<String> symbols = new TreeSet<>();
			for (TradingSecurity security : getSecurities(status)) {
				String symbol = security.getSymbol();
				if (symbol.equals(instrument.getSymbol())) {
					return security;
				}
				symbols.add(symbol);
			}

			// Not found?
			throw new IllegalArgumentException("Instrument not found: " + instrument);

		} catch (Exception e) {
			throw Java.propagate(e);
		}
	}

	public void handleMessage(ISessionStatus message) {
		if (log.isDebugEnabled()) {
			log.debug("Status Message: ({}) '{}'", message.getStatusCode(), message.getStatusMessage());
		}
	}

	private class ResponseListener implements IGenericMessageListener {

		@Override
		public void messageArrived(ITransportable message) {
			handleMessage(message);
		}
	}

	private class StatusListener implements IStatusMessageListener {

		@Override
		public void messageArrived(ISessionStatus message) {
			handleMessage(message);
		}
	}

	@Override
	public boolean subscribe(FxcmInstrument instrument) {
		try {

			MarketDataRequest request = new MarketDataRequest();
			request.addRelatedSymbol(getInstrument(instrument));
			request.setSubscriptionRequestType(SubscriptionRequestTypeFactory.SUBSCRIBE);
			request.setMDEntryTypeSet(MarketDataRequest.MDENTRYTYPESET_ALL);

			// Send the request
			String requestId = gateway.sendMessage(request);

			// Wait for the response
			MarketDataSnapshot snapshot = gatewayResponses.getAndClose(requestId);
			return instrument.getSymbol().equals(snapshot.getInstrument().getSymbol());

		} catch (Exception e) {
			throw Java.propagate(e);
		}

	}

	@SuppressWarnings("unchecked")
	private List<TradingSecurity> getSecurities(TradingSessionStatus status) {
		Enumeration<TradingSecurity> securities = status.getSecurities();
		return Collections.list(securities);
	}

	private Instrument getInstrument(FxcmInstrument instrument) {
		try {

			TradingSessionStatus status = getTradingSessionStatus();

			Set<String> symbols = new TreeSet<>();
			for (TradingSecurity security : getSecurities(status)) {
				String symbol = security.getSymbol();
				if (symbol.equals(instrument.getSymbol())) {
					return security;
				}
				symbols.add(symbol);
			}
			throw new IllegalArgumentException("Instrument not found: " + instrument);

		} catch (Exception e) {
			throw Java.propagate(e);
		}
	}

	@Override
	public IStreamingService getStreamingService() {
		return streaming;
	}

	public class FxcmStreamingService extends StreamingService {

		@Override
		public void register(IInstrumentPriceStream stream) {
			super.registerStream(stream);

			// Subscribe
			FxcmInstrument instrument = (FxcmInstrument) stream.getInstrument();
			subscribe(instrument);
		}

		@Override
		public void unregister(IInstrument instrument) {
		}

		@Override
		public void connect() {
			// Nothing to do
		}

		@Override
		public boolean isConnected() {
			return true;
		}

	}
}
