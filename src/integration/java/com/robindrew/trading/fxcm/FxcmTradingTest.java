package com.robindrew.trading.fxcm;

import static com.robindrew.common.locale.CurrencyCode.GBP;
import static com.robindrew.common.test.UnitTests.getProperty;
import static com.robindrew.trading.fxcm.platform.FxcmEnvironment.DEMO;
import static com.robindrew.trading.trade.TradeDirection.BUY;
import static java.util.concurrent.TimeUnit.MINUTES;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Test;

import com.robindrew.common.util.Threads;
import com.robindrew.trading.fxcm.platform.FxcmCredentials;
import com.robindrew.trading.fxcm.platform.FxcmEnvironment;
import com.robindrew.trading.fxcm.platform.FxcmSession;
import com.robindrew.trading.fxcm.platform.FxcmTradingPlatform;
import com.robindrew.trading.fxcm.platform.api.java.FxcmJavaService;
import com.robindrew.trading.fxcm.platform.api.java.command.getaccounts.FxcmTradingAccount;
import com.robindrew.trading.fxcm.platform.api.java.gateway.FxcmGateway;
import com.robindrew.trading.fxcm.platform.api.java.position.IFxcmPositionService;
import com.robindrew.trading.fxcm.platform.api.java.streaming.IFxcmStreamingService;
import com.robindrew.trading.log.ITransactionLog;
import com.robindrew.trading.log.StubTransactionLog;
import com.robindrew.trading.platform.streaming.IInstrumentPriceStream;
import com.robindrew.trading.position.order.PositionOrderBuilder;
import com.robindrew.trading.price.candle.io.stream.sink.LatestPriceCandleSink;
import com.robindrew.trading.price.candle.io.stream.sink.PriceCandleLoggingStreamSink;

public class FxcmTradingTest {

	@Test
	public void connect() throws Exception {

		String username = getProperty("fxcm.username");
		String password = getProperty("fxcm.password");

		FxcmCredentials credentials = new FxcmCredentials(username, password);
		FxcmEnvironment environment = DEMO;
		FxcmSession session = new FxcmSession(credentials, environment);
		ITransactionLog transactionLog = new StubTransactionLog();

		FxcmGateway gateway = new FxcmGateway(transactionLog);
		FxcmJavaService service = new FxcmJavaService(session, gateway, transactionLog);
		service.login();

		FxcmTradingPlatform platform = new FxcmTradingPlatform(service);

		List<FxcmTradingAccount> accounts = service.getAccounts();
		for (FxcmTradingAccount account : accounts) {
			System.out.println(account);
		}

		IFxcmInstrument instrument = FxcmInstrument.SPOT_GBP_USD;

		IFxcmStreamingService streaming = platform.getStreamingService();
		gateway.setTickHandler(streaming);
		streaming.subscribeToPrices(instrument);

		IInstrumentPriceStream<IFxcmInstrument> stream = streaming.getPriceStream(instrument);
		stream.register(new PriceCandleLoggingStreamSink(instrument));

		LatestPriceCandleSink latest = new LatestPriceCandleSink("LatestPrice");
		stream.register(latest);

		// Wait for a candle
		while (!latest.getLatestCandle().isPresent()) {
			Thread.sleep(10);
		}
		// IPriceCandle candle = latest.getLatestCandle().get();

		// Open a position

		IFxcmPositionService positions = platform.getPositionService();
		PositionOrderBuilder order = new PositionOrderBuilder();
		order.instrument(instrument);
		order.direction(BUY);
		order.tradeCurrency(GBP);
		order.tradeSize(new BigDecimal("0.1"));
		positions.openPosition(order.build());

		Threads.sleep(1, MINUTES);
	}
}