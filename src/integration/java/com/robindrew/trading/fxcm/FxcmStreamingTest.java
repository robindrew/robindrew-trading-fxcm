package com.robindrew.trading.fxcm;

import static com.robindrew.common.test.UnitTests.getProperty;
import static com.robindrew.trading.fxcm.platform.FxcmEnvironment.DEMO;
import static java.util.concurrent.TimeUnit.MINUTES;

import org.junit.Test;

import com.robindrew.common.util.Threads;
import com.robindrew.trading.fxcm.platform.FxcmCredentials;
import com.robindrew.trading.fxcm.platform.FxcmEnvironment;
import com.robindrew.trading.fxcm.platform.FxcmSession;
import com.robindrew.trading.fxcm.platform.api.java.FxcmJavaService;
import com.robindrew.trading.fxcm.platform.api.java.gateway.FxcmGateway;
import com.robindrew.trading.fxcm.platform.api.java.streaming.FxcmStreamingService;
import com.robindrew.trading.log.ITransactionLog;
import com.robindrew.trading.log.StubTransactionLog;
import com.robindrew.trading.platform.streaming.IInstrumentPriceStream;
import com.robindrew.trading.price.candle.io.stream.sink.IPriceCandleStreamSink;
import com.robindrew.trading.price.candle.io.stream.sink.PriceCandleLoggingStreamSink;

public class FxcmStreamingTest {

	@Test
	public void connect() throws Exception {

		String username = getProperty("username");
		String password = getProperty("password");

		FxcmCredentials credentials = new FxcmCredentials(username, password);
		FxcmEnvironment environment = DEMO;
		FxcmSession session = new FxcmSession(credentials, environment);
		ITransactionLog transactionLog = new StubTransactionLog();

		FxcmGateway gateway = new FxcmGateway(transactionLog);
		FxcmJavaService service = new FxcmJavaService(session, gateway, transactionLog);
		service.login();

		FxcmStreamingService streaming = new FxcmStreamingService(service);
		gateway.setTickHandler(streaming);

		IFxcmInstrument instrument = FxcmInstrument.SPOT_EUR_USD;
		streaming.subscribe(instrument);

		IInstrumentPriceStream<IFxcmInstrument> stream = streaming.getPriceStream(instrument);
		IPriceCandleStreamSink sink = new PriceCandleLoggingStreamSink();
		stream.register(sink);

		Threads.sleep(1, MINUTES);
	}
}
