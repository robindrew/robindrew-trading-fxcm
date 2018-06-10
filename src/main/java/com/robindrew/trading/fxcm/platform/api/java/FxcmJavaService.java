package com.robindrew.trading.fxcm.platform.api.java;

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
import com.robindrew.trading.fxcm.IFxcmInstrument;
import com.robindrew.trading.fxcm.platform.IFxcmSession;
import com.robindrew.trading.fxcm.platform.api.java.command.closeposition.ClosePositionCommand;
import com.robindrew.trading.fxcm.platform.api.java.command.getaccounts.FxcmTradingAccount;
import com.robindrew.trading.fxcm.platform.api.java.command.getaccounts.GetAccountsCommand;
import com.robindrew.trading.fxcm.platform.api.java.command.getopenpositions.FxcmPosition;
import com.robindrew.trading.fxcm.platform.api.java.command.getopenpositions.GetOpenPositionsCommand;
import com.robindrew.trading.fxcm.platform.api.java.command.login.LoginCommand;
import com.robindrew.trading.fxcm.platform.api.java.command.logout.LogoutCommand;
import com.robindrew.trading.fxcm.platform.api.java.command.openposition.OpenPositionCommand;
import com.robindrew.trading.fxcm.platform.api.java.command.tradingsessionstatus.TradingSessionStatusCommand;
import com.robindrew.trading.fxcm.platform.api.java.gateway.FxcmGateway;
import com.robindrew.trading.log.ITransactionLog;
import com.robindrew.trading.position.order.IPositionOrder;

public class FxcmJavaService implements IFxcmJavaService {

	private static final Logger log = LoggerFactory.getLogger(FxcmJavaService.class);

	private final IFxcmSession session;
	private final FxcmGateway gateway;
	private final ITransactionLog transactionLog;

	public FxcmJavaService(IFxcmSession session, FxcmGateway gateway, ITransactionLog transactionLog) {
		this.session = Check.notNull("session", session);
		this.transactionLog = Check.notNull("transactionLog", transactionLog);
		this.gateway = Check.notNull("gateway", gateway);
	}

	@Override
	public IFxcmSession getSession() {
		return session;
	}

	@Override
	public ITransactionLog getTransactionLog() {
		return transactionLog;
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
	public boolean unsubscribe(IFxcmInstrument instrument) {
		try {

			MarketDataRequest request = new MarketDataRequest();
			request.addRelatedSymbol(getInstrument(instrument));
			request.setSubscriptionRequestType(SubscriptionRequestTypeFactory.UNSUBSCRIBE);
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

	@Override
	public boolean subscribe(IFxcmInstrument instrument) {
		log.info("[Subscribe] {}", instrument);
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

	private Instrument getInstrument(IFxcmInstrument instrument) {
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

}
