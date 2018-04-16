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
import com.robindrew.common.util.Java;
import com.robindrew.trading.fxcm.FxcmInstrument;
import com.robindrew.trading.fxcm.platform.rest.response.GatewayResponseCache;
import com.robindrew.trading.price.candle.IPriceCandle;

public class FxcmGateway {

	private static final Logger log = LoggerFactory.getLogger(FxcmGateway.class);

	private final IGateway gateway;
	private final GatewayResponseCache responseCache;
	private final ResponseListener responseListener;
	private final StatusListener statusListener;

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

	public void handleMessage(ITransportable message) {
		try {
			String requestId = message.getRequestID();

			// Is this message a request/response
			if (requestId != null) {
				log.info("[Message] requestId={}", requestId);
				log.info("[Message] type={}", message.getClass().getName());
				log.info("[Message] content={}", message);
				responseCache.put(requestId, message);
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

}
