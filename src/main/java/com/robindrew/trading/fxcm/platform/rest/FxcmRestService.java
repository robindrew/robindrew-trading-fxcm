package com.robindrew.trading.fxcm.platform.rest;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Future;

import com.fxcm.fix.Instrument;
import com.fxcm.fix.SubscriptionRequestTypeFactory;
import com.fxcm.fix.TradingSecurity;
import com.fxcm.fix.pretrade.MarketDataRequest;
import com.fxcm.fix.pretrade.MarketDataSnapshot;
import com.fxcm.fix.pretrade.TradingSessionStatus;
import com.robindrew.common.util.Check;
import com.robindrew.common.util.Java;
import com.robindrew.trading.IInstrument;
import com.robindrew.trading.fxcm.FxcmInstrument;
import com.robindrew.trading.fxcm.platform.IFxcmSession;
import com.robindrew.trading.fxcm.platform.rest.closeposition.ClosePositionCommand;
import com.robindrew.trading.fxcm.platform.rest.getaccounts.FxcmTradingAccount;
import com.robindrew.trading.fxcm.platform.rest.getaccounts.GetAccountsCommand;
import com.robindrew.trading.fxcm.platform.rest.getopenpositions.FxcmPosition;
import com.robindrew.trading.fxcm.platform.rest.getopenpositions.GetOpenPositionsCommand;
import com.robindrew.trading.fxcm.platform.rest.login.LoginCommand;
import com.robindrew.trading.fxcm.platform.rest.logout.LogoutCommand;
import com.robindrew.trading.fxcm.platform.rest.openposition.OpenPositionCommand;
import com.robindrew.trading.fxcm.platform.rest.tradingsessionstatus.TradingSessionStatusCommand;
import com.robindrew.trading.log.ITransactionLog;
import com.robindrew.trading.platform.streaming.IInstrumentPriceStream;
import com.robindrew.trading.platform.streaming.IStreamingService;
import com.robindrew.trading.platform.streaming.StreamingService;
import com.robindrew.trading.position.order.IPositionOrder;

public class FxcmRestService implements IFxcmRestService {

	private final IFxcmSession session;
	private final FxcmGateway gateway;
	private final FxcmStreamingService streaming = new FxcmStreamingService();
	private final ITransactionLog transactions;

	public FxcmRestService(IFxcmSession session, ITransactionLog transactions) {
		this.session = Check.notNull("session", session);
		this.transactions = Check.notNull("transactions", transactions);
		this.gateway = new FxcmGateway(transactions);
	}

	public ITransactionLog getTransactionLog() {
		return transactions;
	}

	@Override
	public IStreamingService getStreamingService() {
		return streaming;
	}

	@Override
	public void login() {
		new LoginCommand(session).execute(gateway);
		getTradingSessionStatus();
	}

	@Override
	public void logout() {
		new LogoutCommand().execute(gateway);
	}

	@Override
	public TradingSessionStatus getTradingSessionStatus() {
		return new TradingSessionStatusCommand().execute(gateway);
	}

	public void openPosition(IPositionOrder order) {
		new OpenPositionCommand(order).execute(gateway);
	}

	public void closePosition(FxcmPosition position) {
		new ClosePositionCommand(position).execute(gateway);
	}

	public List<FxcmTradingAccount> getAccounts() {
		return new GetAccountsCommand().execute(gateway);
	}

	public List<FxcmPosition> getPositions() {
		return new GetOpenPositionsCommand().execute(gateway);
	}

	public Future<MarketDataSnapshot> getMarketDataSnapshot(FxcmInstrument instrument) {
		try {

			TradingSessionStatus status = getTradingSessionStatus();

			MarketDataRequest marketData = new MarketDataRequest();
			marketData.addRelatedSymbol(getInstrument(status, instrument));
			marketData.setSubscriptionRequestType(SubscriptionRequestTypeFactory.SNAPSHOT);
			marketData.setMDEntryTypeSet(MarketDataRequest.MDENTRYTYPESET_TICKALL);

			// Request / Response
			return gateway.execute(marketData);

		} catch (Exception e) {
			throw Java.propagate(e);
		}
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

	@Override
	public boolean subscribe(FxcmInstrument instrument) {
		try {

			MarketDataRequest request = new MarketDataRequest();
			request.addRelatedSymbol(getInstrument(instrument));
			request.setSubscriptionRequestType(SubscriptionRequestTypeFactory.SUBSCRIBE);
			request.setMDEntryTypeSet(MarketDataRequest.MDENTRYTYPESET_ALL);

			// Send the request
			String requestId = gateway.getGateway().sendMessage(request);

			// Wait for the response
			MarketDataSnapshot snapshot = gateway.getResponseCache().getAndClose(requestId);
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
