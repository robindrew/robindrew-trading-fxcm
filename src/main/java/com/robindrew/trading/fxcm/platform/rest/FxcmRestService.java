package com.robindrew.trading.fxcm.platform.rest;

import static com.robindrew.trading.price.decimal.Decimals.toInt;

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
import com.fxcm.fix.Instrument;
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
import com.robindrew.trading.IInstrument;
import com.robindrew.trading.fxcm.FxcmInstrument;
import com.robindrew.trading.fxcm.platform.IFxcmSession;
import com.robindrew.trading.fxcm.platform.rest.TransportableCache.TransportableFuture;
import com.robindrew.trading.platform.streaming.IInstrumentPriceStream;
import com.robindrew.trading.platform.streaming.IStreamingService;
import com.robindrew.trading.platform.streaming.StreamingService;
import com.robindrew.trading.price.candle.IPriceCandle;
import com.robindrew.trading.price.candle.PriceCandle;
import com.robindrew.trading.price.candle.TickPriceCandle;

public class FxcmRestService implements IFxcmRestService {

	private static final Logger log = LoggerFactory.getLogger(FxcmRestService.class);

	private final IFxcmSession session;
	private final IGateway gateway;
	private final TransportableCache gatewayAsyncResponseMap = new TransportableCache();
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

			// Immediately get the session status
			getTradingSessionStatus();

		} catch (Exception e) {
			throw Java.propagate(e);
		}
	}

	@Override
	public void logout() {
		gateway.logout();
	}

	@Override
	public TradingSessionStatus getTradingSessionStatus() {
		log.info("getTradingSessionStatus()");
		Stopwatch timer = Stopwatch.createStarted();
		TradingSessionStatus status = getTradingSessionStatusAsync().get();
		timer.stop();
		log.info("getTradingSessionStatus() took {}", timer);
		return status;
	}

	@Override
	public TransportableFuture<TradingSessionStatus> getTradingSessionStatusAsync() {
		String requestId = gateway.requestTradingSessionStatus();
		return gatewayAsyncResponseMap.get(requestId);
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
			return gatewayAsyncResponseMap.get(requestId);

		} catch (Exception e) {
			throw Java.propagate(e);
		}
	}

	public void handleMessage(ITransportable message) {
		try {

			// Is this message a request/response
			String requestId = message.getRequestID();
			if (requestId != null) {
				log.debug("messageArrived(requestId={})", requestId);
				gatewayAsyncResponseMap.put(requestId, message);
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
		FxcmInstrument instrument = getFxcmInstrument(snapshot.getInstrument());
		IPriceCandle candle = toPriceCandle(snapshot);
		handlePriceTick(instrument, candle);
	}

	private void handlePriceTick(FxcmInstrument instrument, IPriceCandle candle) {
		log.info("[{}] {}", instrument, candle);
	}

	protected FxcmInstrument getFxcmInstrument(Instrument instrument) throws Exception {
		String symbol = instrument.getSymbol();
		return FxcmInstrument.valueOf(symbol);
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

	private IPriceCandle toPriceCandle(MarketDataSnapshot snapshot) throws Exception {

		// Instrument
		FxcmInstrument instrument = getFxcmInstrument(snapshot.getInstrument());

		// Decimal Places
		int decimalPlaces = instrument.getPricePrecision().getDecimalPlaces();

		// Bid Prices
		int bidOpenPrice = toInt(snapshot.getBidOpen(), decimalPlaces);
		int bidHighPrice = toInt(snapshot.getBidHigh(), decimalPlaces);
		int bidLowPrice = toInt(snapshot.getBidLow(), decimalPlaces);
		int bidClosePrice = toInt(snapshot.getBidClose(), decimalPlaces);

		// Ask Prices
		int askOpenPrice = toInt(snapshot.getAskOpen(), decimalPlaces);
		int askHighPrice = toInt(snapshot.getAskHigh(), decimalPlaces);
		int askLowPrice = toInt(snapshot.getAskLow(), decimalPlaces);
		int askClosePrice = toInt(snapshot.getAskClose(), decimalPlaces);

		// Timestamps (UTC)
		long openTime = snapshot.getOpenTimestamp().getTime();
		long closeTime = snapshot.getCloseTimestamp().getTime();

		// Is this a tick?
		if (openTime == closeTime) {
			if ((bidOpenPrice == bidClosePrice) && (bidHighPrice == bidLowPrice)) {
				if ((askOpenPrice == askClosePrice) && (askHighPrice == askLowPrice)) {
					return new TickPriceCandle(bidClosePrice, askClosePrice, closeTime, decimalPlaces);
				}
			}
		}

		// Nope, just a big fat candle!
		return new PriceCandle(bidOpenPrice, bidHighPrice, bidLowPrice, bidClosePrice, askOpenPrice, askHighPrice, askLowPrice, askClosePrice, openTime, closeTime, decimalPlaces);
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
			ITransportable response = gatewayAsyncResponseMap.get(requestId).get();

			// We are expecting a price snapshot
			if (response instanceof MarketDataSnapshot) {
				MarketDataSnapshot snapshot = (MarketDataSnapshot) response;
				return instrument.getSymbol().equals(snapshot.getInstrument().getSymbol());
			}
			return false;

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
