package com.robindrew.trading.fxcm;

import static com.robindrew.trading.fxcm.platform.FxcmEnvironment.DEMO;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.File;
import java.math.BigDecimal;

import org.junit.Test;

import com.robindrew.common.locale.CurrencyCode;
import com.robindrew.common.util.Threads;
import com.robindrew.trading.fxcm.platform.FxcmCredentials;
import com.robindrew.trading.fxcm.platform.FxcmEnvironment;
import com.robindrew.trading.fxcm.platform.FxcmSession;
import com.robindrew.trading.fxcm.platform.rest.FxcmRestService;
import com.robindrew.trading.fxcm.platform.rest.getopenpositions.FxcmPosition;
import com.robindrew.trading.log.TransactionLog;
import com.robindrew.trading.position.order.IPositionOrder;
import com.robindrew.trading.position.order.PositionOrder;
import com.robindrew.trading.trade.TradeDirection;

public class FxcmConnectionTest {

	@Test
	public void connect() throws Exception {

		String username = System.getProperty("username");
		String password = System.getProperty("password");

		FxcmCredentials credentials = new FxcmCredentials(username, password);
		FxcmEnvironment environment = DEMO;
		FxcmSession session = new FxcmSession(credentials, environment);
		TransactionLog transactionLog = new TransactionLog(new File("c:/temp/"));
		transactionLog.start();

		FxcmRestService rest = new FxcmRestService(session, transactionLog);
		rest.login();
		rest.getAccounts();

		FxcmInstrument instrument = FxcmInstrument.SPOT_EUR_USD;
		rest.getMarketDataSnapshot(instrument);

		TradeDirection direction = TradeDirection.BUY;
		CurrencyCode currency = CurrencyCode.GBP;
		BigDecimal tradeSize = new BigDecimal("1000");
		IPositionOrder order = new PositionOrder(instrument, direction, currency, tradeSize, 0, 0);
		rest.openPosition(order);

		Threads.sleep(10, SECONDS);

		for (FxcmPosition position : rest.getPositions()) {
			rest.closePosition(position);
		}

		Threads.sleep(10, SECONDS);
	}
}
