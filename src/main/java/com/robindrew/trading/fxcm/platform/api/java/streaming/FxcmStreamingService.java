package com.robindrew.trading.fxcm.platform.api.java.streaming;

import static com.robindrew.trading.provider.TradingProvider.FXCM;

import com.robindrew.trading.fxcm.IFxcmInstrument;
import com.robindrew.trading.fxcm.platform.api.java.FxcmJavaService;
import com.robindrew.trading.platform.streaming.AbstractStreamingService;
import com.robindrew.trading.platform.streaming.IInstrumentPriceStream;
import com.robindrew.trading.platform.streaming.InstrumentPriceStream;
import com.robindrew.trading.price.candle.tick.ITickPriceCandle;

public class FxcmStreamingService extends AbstractStreamingService<IFxcmInstrument> implements IFxcmStreamingService {

	private final FxcmJavaService service;

	public FxcmStreamingService(FxcmJavaService service) {
		super(FXCM);
		this.service = service;
		
		// Register with the gateway
		service.getGateway().setTickHandler(this);
	}

	@Override
	public synchronized boolean subscribeToPrices(IFxcmInstrument instrument) {
		if (isSubscribedInstrument(instrument)) {
			return true;
		}
		if (service.subscribe(instrument)) {
			registerStream(instrument);
			return true;
		}
		return false;
	}

	private synchronized void registerStream(IFxcmInstrument instrument) {
		// Create a simple stream
		InstrumentPriceStream<IFxcmInstrument> stream = new InstrumentPriceStream<>(instrument);
		registerStream(stream);
	}

	@Override
	public synchronized boolean unsubscribeFromPrices(IFxcmInstrument instrument) {
		return service.unsubscribe(instrument);
	}

	@Override
	public void handleTick(IFxcmInstrument instrument, ITickPriceCandle candle) {
		if (!isSubscribedInstrument(instrument)) {
			registerStream(instrument);
		}

		// Handle the tick
		IInstrumentPriceStream<IFxcmInstrument> stream = getPriceStream(instrument);
		stream.putNextCandle(candle);
	}
}