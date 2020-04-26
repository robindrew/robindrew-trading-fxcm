package com.robindrew.trading.fxcm;

import static java.util.concurrent.TimeUnit.MINUTES;

import org.junit.Test;

import com.robindrew.common.util.Threads;
import com.robindrew.trading.fxcm.platform.FxcmTradingPlatform;
import com.robindrew.trading.fxcm.platform.FxcmTradingPlatformBuilder;
import com.robindrew.trading.fxcm.platform.api.java.streaming.IFxcmStreamingService;
import com.robindrew.trading.platform.streaming.IInstrumentPriceStream;
import com.robindrew.trading.price.candle.io.stream.sink.IPriceCandleStreamSink;
import com.robindrew.trading.price.candle.io.stream.sink.PriceCandleLoggingStreamSink;

public class FxcmStreamingTest {

	@Test
	public void connect() throws Exception {

		IFxcmInstrument instrument = FxcmInstrument.SPOT_GBP_USD;

		FxcmTradingPlatform platform = new FxcmTradingPlatformBuilder().get();

		IFxcmStreamingService streaming = platform.getStreamingService();
		streaming.subscribeToPrices(instrument);

		IInstrumentPriceStream<IFxcmInstrument> stream = streaming.getPriceStream(instrument);
		IPriceCandleStreamSink sink = new PriceCandleLoggingStreamSink(instrument);
		stream.register(sink);

		Threads.sleep(1, MINUTES);
	}
}
