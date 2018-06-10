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
import com.robindrew.trading.fxcm.platform.api.java.FxcmJavaService;
import com.robindrew.trading.fxcm.platform.api.java.command.getopenpositions.FxcmPosition;
import com.robindrew.trading.fxcm.platform.api.java.gateway.FxcmGateway;
import com.robindrew.trading.log.FileBackedTransactionLog;
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
		FileBackedTransactionLog transactionLog = new FileBackedTransactionLog(new File("c:/temp/"));
		transactionLog.start("FxcmTransactionLog");

		FxcmGateway gateway = new FxcmGateway(transactionLog);
		FxcmJavaService service = new FxcmJavaService(session, gateway, transactionLog);
		service.login();
		service.getAccounts();
		for (String name : service.getInstrumentNames()) {
			System.out.println(name);
		}

		FxcmInstrument instrument = FxcmInstrument.SPOT_EUR_USD;
		service.getMarketDataSnapshot(instrument);

		TradeDirection direction = TradeDirection.BUY;
		CurrencyCode currency = CurrencyCode.GBP;
		BigDecimal tradeSize = new BigDecimal("1000");
		IPositionOrder order = new PositionOrder(instrument, direction, currency, tradeSize, 0, 0);
		service.openPosition(order);

		Threads.sleep(10, SECONDS);

		for (FxcmPosition position : service.getPositions()) {
			service.closePosition(position);
		}

		Threads.sleep(10, SECONDS);
	}
}
