package com.robindrew.trading.fxcm;

import static com.robindrew.trading.fxcm.platform.FxcmEnvironment.DEMO;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.File;
import java.math.BigDecimal;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static final Logger log = LoggerFactory.getLogger(FxcmConnectionTest.class);

	@Test
	public void connect() throws Exception {

		String username = System.getProperty("fxcm.username");
		String password = System.getProperty("fxcm.password");

		FxcmCredentials credentials = new FxcmCredentials(username, password);
		FxcmEnvironment environment = DEMO;
		FxcmSession session = new FxcmSession(credentials, environment);
		FileBackedTransactionLog transactionLog = new FileBackedTransactionLog(new File("c:/temp/"));
		transactionLog.start("FxcmTransactionLog");

		FxcmGateway gateway = new FxcmGateway(transactionLog);
		FxcmJavaService service = new FxcmJavaService(session, gateway, transactionLog);
		service.login();
		service.getAccounts();
		for (FxcmInstrument instrument : service.getInstruments()) {
			log.info("Instrument: {}", instrument);
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
