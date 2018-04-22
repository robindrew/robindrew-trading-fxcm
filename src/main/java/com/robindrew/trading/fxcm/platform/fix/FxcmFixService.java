package com.robindrew.trading.fxcm.platform.fix;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fxcm.fix.Instrument;
import com.fxcm.fix.SubscriptionRequestTypeFactory;
import com.fxcm.fix.TradingSecurity;
import com.fxcm.fix.pretrade.MarketDataRequest;
import com.fxcm.fix.pretrade.MarketDataSnapshot;
import com.fxcm.fix.pretrade.TradingSessionStatus;
import com.robindrew.common.util.Check;
import com.robindrew.common.util.Java;
import com.robindrew.trading.fxcm.FxcmInstrument;
import com.robindrew.trading.fxcm.platform.IFxcmSession;
import com.robindrew.trading.fxcm.platform.fix.closeposition.ClosePositionCommand;
import com.robindrew.trading.fxcm.platform.fix.getaccounts.FxcmTradingAccount;
import com.robindrew.trading.fxcm.platform.fix.getaccounts.GetAccountsCommand;
import com.robindrew.trading.fxcm.platform.fix.getopenpositions.FxcmPosition;
import com.robindrew.trading.fxcm.platform.fix.getopenpositions.GetOpenPositionsCommand;
import com.robindrew.trading.fxcm.platform.fix.login.LoginCommand;
import com.robindrew.trading.fxcm.platform.fix.logout.LogoutCommand;
import com.robindrew.trading.fxcm.platform.fix.openposition.OpenPositionCommand;
import com.robindrew.trading.fxcm.platform.fix.tradingsessionstatus.TradingSessionStatusCommand;
import com.robindrew.trading.log.ITransactionLog;
import com.robindrew.trading.platform.streaming.IStreamingService;
import com.robindrew.trading.platform.streaming.StreamingService;
import com.robindrew.trading.position.order.IPositionOrder;

public class FxcmFixService implements IFxcmRestService {

	private static final Logger log = LoggerFactory.getLogger(FxcmFixService.class);

	private final IFxcmSession session;
	private final FxcmGateway gateway;
	private final FxcmStreamingService streaming = new FxcmStreamingService();
	private final ITransactionLog transactions;

	public FxcmFixService(IFxcmSession session, ITransactionLog transactions) {
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

	@SuppressWarnings("unchecked")
	public Set<String> getInstrumentNames() {
		TradingSessionStatus status = getTradingSessionStatus();
		Set<String> names = new TreeSet<>();
		List<TradingSecurity> securities = Collections.list(status.getSecurities());
		for (TradingSecurity security : securities) {
			try {
				names.add(security.getSymbol());
			} catch (Exception e) {
				log.warn("Failed to get symbol for security: " + security);
			}
		}
		return names;
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

	public MarketDataSnapshot getMarketDataSnapshot(FxcmInstrument instrument) {
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

	public class FxcmStreamingService extends StreamingService<FxcmInstrument> {

		@Override
		public boolean subscribe(FxcmInstrument instrument) {
			return FxcmFixService.this.subscribe(instrument);
		}

		@Override
		public boolean unsubscribe(FxcmInstrument instrument) {
			return false;
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
