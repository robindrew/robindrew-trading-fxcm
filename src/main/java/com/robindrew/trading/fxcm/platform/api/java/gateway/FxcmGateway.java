package com.robindrew.trading.fxcm.platform.api.java.gateway;

import java.util.Optional;

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
import com.robindrew.trading.fxcm.platform.api.java.command.getaccounts.FxcmTradingAccount;
import com.robindrew.trading.fxcm.platform.api.java.command.response.GatewayResponseCache;
import com.robindrew.trading.log.ITransactionLog;

public class FxcmGateway {

	private static final Logger log = LoggerFactory.getLogger(FxcmGateway.class);

	private final IGateway gateway;
	private final GatewayResponseCache responseCache;
	private final ResponseListener responseListener;
	private final StatusListener statusListener;
	private final ITransactionLog transactions;
	private final MarketDataSnapshotListener snapshotListener;

	private volatile CollateralReport latestCollateralReport = null;
	private volatile String defaultAccount;

	public FxcmGateway(ITransactionLog transactions) {

		this.gateway = GatewayFactory.createGateway();
		this.responseCache = new GatewayResponseCache();
		this.responseListener = new ResponseListener();
		this.statusListener = new StatusListener();
		this.transactions = Check.notNull("transactions", transactions);
		this.snapshotListener = new MarketDataSnapshotListener();

		// Register listeners
		gateway.registerGenericMessageListener(responseListener);
		gateway.registerStatusMessageListener(statusListener);

		// Start the snapshot listener
		snapshotListener.start();
	}

	public void setTickHandler(IFxcmGatewayTickHandler handler) {
		snapshotListener.setTickHandler(handler);
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

	public Optional<CollateralReport> getLatestCollateralReport() {
		return Optional.ofNullable(latestCollateralReport);
	}

	public void handleResponse(ITransportable response) {
		logMessage("Response", response);

		try {
			String requestId = response.getRequestID();

			// Is this message a request/response
			if (requestId != null) {
				responseCache.put(requestId, response);
				return;
			}

			// Handle published price snapshots (ticking prices)
			// We do not record these in the transaction log - there are loads!
			if (response instanceof MarketDataSnapshot) {
				if (snapshotListener != null) {
					snapshotListener.handleSnapshot((MarketDataSnapshot) response);
				}
				return;
			}

			// How should we handle published Collateral Reports?
			// These occur after opening or closing a position (possibly other actions too)
			if (response instanceof CollateralReport) {
				latestCollateralReport = (CollateralReport) response;
				return;
			}

			// Warning of unhandled messages
			log.warn("[Unhandled Response] " + response);

		} catch (Exception e) {
			throw Java.propagate(e);
		}
	}

	public void handleStatus(ISessionStatus status) {
		logMessage("Status", status);
	}

	private class ResponseListener implements IGenericMessageListener {

		@Override
		public void messageArrived(ITransportable message) {
			if (message != null) {
				handleResponse(message);
			}
		}
	}

	private class StatusListener implements IStatusMessageListener {

		@Override
		public void messageArrived(ISessionStatus message) {
			if (message != null) {
				handleStatus(message);
			}
		}
	}

}
