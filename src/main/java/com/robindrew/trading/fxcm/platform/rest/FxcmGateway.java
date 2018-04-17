package com.robindrew.trading.fxcm.platform.rest;

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
import com.robindrew.common.util.Check;
import com.robindrew.common.util.Java;
import com.robindrew.trading.fxcm.FxcmInstrument;
import com.robindrew.trading.fxcm.platform.rest.getaccounts.FxcmTradingAccount;
import com.robindrew.trading.fxcm.platform.rest.response.GatewayResponseCache;
import com.robindrew.trading.log.ITransactionLog;
import com.robindrew.trading.price.candle.IPriceCandle;

public class FxcmGateway {

	private static final Logger log = LoggerFactory.getLogger(FxcmGateway.class);

	private final IGateway gateway;
	private final GatewayResponseCache responseCache;
	private final ResponseListener responseListener;
	private final StatusListener statusListener;
	private final ITransactionLog transactions;

	private volatile String defaultAccount;

	public FxcmGateway(ITransactionLog transactions) {
		this.gateway = GatewayFactory.createGateway();
		this.responseCache = new GatewayResponseCache();
		this.responseListener = new ResponseListener();
		this.statusListener = new StatusListener();
		this.transactions = Check.notNull("transactions", transactions);

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
			String requestId = sendMessage(message);
			return responseCache.getAndClose(requestId);
		} catch (Exception e) {
			throw Java.propagate(e);
		}
	}

	private String sendMessage(ITransportable message) throws Exception {
		logMessage("sendMessage", message);
		return gateway.sendMessage(message);
	}

	private void logMessage(String method, Object message) {
		String type = message.getClass().getSimpleName();
		transactions.log(method + "(" + type + ")", message);
	}

	public void handleResponse(ITransportable response) {
		try {
			logMessage("messageArrived", response);

			String requestId = response.getRequestID();

			// Is this message a request/response
			if (requestId != null) {
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
		FxcmInstrument instrument = toFxcmInstrument(snapshot.getInstrument());
		IPriceCandle candle = toPriceCandle(snapshot);
		handlePriceTick(instrument, candle);
	}

	private void handlePriceTick(FxcmInstrument instrument, IPriceCandle candle) {
	}

	public void handleStatus(ISessionStatus status) {
		String type = status.getClass().getSimpleName();
		transactions.log("messageArrived(" + type + ")", status);
		if (log.isDebugEnabled()) {
			log.debug("Status Message: ({}) '{}'", status.getStatusCode(), status.getStatusMessage());
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
			handleStatus(message);
		}
	}

}
