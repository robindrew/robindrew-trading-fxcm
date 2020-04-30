package com.robindrew.trading.fxcm;

import static com.robindrew.common.locale.CurrencyCode.GBP;
import static com.robindrew.trading.position.order.PositionOrderBuilder.orderBuilder;
import static com.robindrew.trading.trade.TradeDirection.BUY;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.robindrew.trading.fxcm.platform.FxcmTradingPlatform;
import com.robindrew.trading.fxcm.platform.FxcmTradingPlatformBuilder;
import com.robindrew.trading.fxcm.platform.api.java.command.getaccounts.FxcmTradingAccount;
import com.robindrew.trading.fxcm.platform.api.java.position.IFxcmPositionService;
import com.robindrew.trading.fxcm.platform.api.java.streaming.IFxcmStreamingService;
import com.robindrew.trading.platform.streaming.IInstrumentPriceStream;
import com.robindrew.trading.position.IPosition;
import com.robindrew.trading.position.order.IPositionOrder;
import com.robindrew.trading.price.candle.io.stream.sink.LatestPriceCandleSink;
import com.robindrew.trading.price.candle.io.stream.sink.PriceCandleLoggingStreamSink;

public class FxcmTradingTest {

	private static final Logger log = LoggerFactory.getLogger(FxcmTradingTest.class);

	@Test
	public void connect() throws Exception {

		IFxcmInstrument instrument = FxcmInstrument.SPOT_AUD_USD;

		FxcmTradingPlatform platform = new FxcmTradingPlatformBuilder().get();

		List<FxcmTradingAccount> accounts = platform.getJavaService().getAccounts();
		for (FxcmTradingAccount account : accounts) {
			System.out.println(account);
		}

		IFxcmStreamingService streaming = platform.getStreamingService();
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

		List<IPosition> positionList = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			Stopwatch timer = Stopwatch.createStarted();
			IPositionOrder order = orderBuilder().instrument(instrument).direction(BUY).tradeCurrency(GBP).tradeSize(1000).build();
			IPosition position = positions.openPosition(order);
			positionList.add(position);
			timer.stop();
			log.info("Position {} opened in {}", position.getId(), timer);
		}

		for (IPosition position : positionList) {
			positions.closePosition(position);
		}
	}
}