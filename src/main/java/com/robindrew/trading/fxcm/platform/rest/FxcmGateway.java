package com.robindrew.trading.fxcm.platform.rest;

import static com.robindrew.common.text.Strings.json;
import static com.robindrew.trading.fxcm.platform.rest.FxcmRest.toFxcmInstrument;
import static com.robindrew.trading.fxcm.platform.rest.FxcmRest.toPriceCandle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fxcm.external.api.transport.GatewayFactory;
import com.fxcm.external.api.transport.IGateway;
import com.fxcm.external.api.transport.listeners.IGenericMessageListener;
import com.fxcm.external.api.transport.listeners.IStatusMessageListener;
import com.fxcm.fix.pretrade.MarketDataSnapshot;
import com.fxcm.messaging.ISessionStatus;
import com.fxcm.messaging.ITransportable;
import com.robindrew.common.text.Strings;
import com.robindrew.common.util.Check;
import com.robindrew.common.util.Java;
import com.robindrew.trading.fxcm.FxcmInstrument;
import com.robindrew.trading.fxcm.platform.rest.getaccounts.FxcmTradingAccount;
import com.robindrew.trading.fxcm.platform.rest.response.GatewayResponseCache;
import com.robindrew.trading.price.candle.IPriceCandle;

public class FxcmGateway {

	private static final Logger log = LoggerFactory.getLogger(FxcmGateway.class);

	private final IGateway gateway;
	private final GatewayResponseCache responseCache;
	private final ResponseListener responseListener;
	private final StatusListener statusListener;
	private volatile String defaultAccount;

	public FxcmGateway() {
		this.gateway = GatewayFactory.createGateway();
		this.responseCache = new GatewayResponseCache();
		this.responseListener = new ResponseListener();
		this.statusListener = new StatusListener();

		// Register listeners
		gateway.registerGenericMessageListener(responseListener);
		gateway.registerStatusMessageListener(statusListener);
	}

	public IGateway getGateway() {
		return gateway;
	}

	public GatewayResponseCache getResponseCache() {
		return responseCache;
	}

	public String getDefaultAccount() {
		if (defaultAccount == null) {
			throw new IllegalStateException("defaultAccount not set");
		}
		return defaultAccount;
	}

	public void setDefaultAccount(String account) {
		this.defaultAccount = Check.notEmpty("account", account);
	}

	public void setDefaultAccount(FxcmTradingAccount account) {
		this.defaultAccount = account.getId();
	}

	public ResponseListener getResponseListener() {
		return responseListener;
	}

	public StatusListener getStatusListener() {
		return statusListener;
	}

	public <T extends ITransportable> T execute(ITransportable message) {
		try {
			String requestId = gateway.sendMessage(message);
			return responseCache.getAndClose(requestId);
		} catch (Exception e) {
			throw Java.propagate(e);
		}
	}

	public void handleResponse(ITransportable response) {
		try {
			String requestId = response.getRequestID();

			// Is this message a request/response
			if (requestId != null) {
				log.debug("[Response] {}\n{}", response.getClass().getSimpleName(), json(response));
				responseCache.put(requestId, response);
				return;
			}

			// Handle snapshots (ticking prices)
			if (response instanceof MarketDataSnapshot) {
				handleMarketDataSnapshot((MarketDataSnapshot) response);
				return;
			}

			log.warn("Message not handled: " + response);

		} catch (Exception e) {
			throw Java.propagate(e);
		}
	}

	private void handleMarketDataSnapshot(MarketDataSnapshot snapshot) throws Exception {
		log.info("[Tick]\n{}", Strings.json(snapshot, true));

		FxcmInstrument instrument = toFxcmInstrument(snapshot.getInstrument());
		IPriceCandle candle = toPriceCandle(snapshot);
		handlePriceTick(instrument, candle);
	}

	private void handlePriceTick(FxcmInstrument instrument, IPriceCandle candle) {
	}

	public void handleMessage(ISessionStatus message) {
		if (log.isDebugEnabled()) {
			log.debug("Status Message: ({}) '{}'", message.getStatusCode(), message.getStatusMessage());
		}
	}

	private class ResponseListener implements IGenericMessageListener {

		@Override
		public void messageArrived(ITransportable message) {
			handleResponse(message);
		}
	}

	private class StatusListener implements IStatusMessageListener {

		@Override
		public void messageArrived(ISessionStatus message) {
			handleMessage(message);
		}
	}

}
