package com.robindrew.trading.fxcm.platform.fix;

import static com.robindrew.trading.fxcm.platform.fix.FxcmFix.toFxcmInstrument;
import static com.robindrew.trading.fxcm.platform.fix.FxcmFix.toPriceCandle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fxcm.external.api.transport.GatewayFactory;
import com.fxcm.external.api.transport.IGateway;
import com.fxcm.external.api.transport.listeners.IGenericMessageListener;
import com.fxcm.external.api.transport.listeners.IStatusMessageListener;
import com.fxcm.fix.posttrade.CollateralReport;
import com.fxcm.fix.pretrade.MarketDataSnapshot;
import com.fxcm.messaging.ISessionStatus;
import com.fxcm.messaging.ITransportable;
import com.robindrew.common.util.Check;
import com.robindrew.common.util.Java;
import com.robindrew.trading.fxcm.FxcmInstrument;
import com.robindrew.trading.fxcm.platform.fix.getaccounts.FxcmTradingAccount;
import com.robindrew.trading.fxcm.platform.fix.response.GatewayResponseCache;
import com.robindrew.trading.log.ITransactionLog;
import com.robindrew.trading.price.candle.IPriceCandle;

public class FxcmGateway {

	private static final Logger log = LoggerFactory.getLogger(FxcmGateway.class);

	private final IGateway gateway;
	private final GatewayResponseCache responseCache;
	private final ResponseListener responseListener;
	private final StatusListener statusListener;
	private final ITransactionLog transactions;
	private volatile CollateralReport latestCollateralReport = null;

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
		String requestId = sendMessage(message);
		return responseCache.getAndClose(requestId);
	}

	public String sendMessage(ITransportable message) {
		logMessage("Request", message);
		try {
			return gateway.sendMessage(message);
		} catch (Exception e) {
			throw Java.propagate(e);
		}
	}

	private void logMessage(String method, Object message) {
		String type = message.getClass().getSimpleName();
		transactions.log(method + "(" + type + ")", message);
	}

	public void handleResponse(ITransportable response) {
		try {
			String requestId = response.getRequestID();

			// Is this message a request/response
			if (requestId != null) {
				logMessage("Response", response);
				responseCache.put(requestId, response);
				return;
			}

			// Handle published price snapshots (ticking prices)
			// We do not record these in the transaction log - there are loads!
			if (response instanceof MarketDataSnapshot) {
				handleMarketDataSnapshot((MarketDataSnapshot) response);
				return;
			}

			// How should we handle published Collateral Reports?
			// These occur after opening or closing a position (possibly other actions too)
			if (response instanceof CollateralReport) {
				// TODO: Handling reports 
				latestCollateralReport = (CollateralReport) response;
			}

			// Log unhandled messages so we can review them
			logMessage("Published", response);
			log.warn("Published message not handled: " + response);

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
