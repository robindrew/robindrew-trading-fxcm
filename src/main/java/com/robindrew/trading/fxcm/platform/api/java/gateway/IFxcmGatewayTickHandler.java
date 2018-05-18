package com.robindrew.trading.fxcm.platform.api.java.gateway;

import com.robindrew.trading.fxcm.IFxcmInstrument;
import com.robindrew.trading.price.candle.ITickPriceCandle;

public interface IFxcmGatewayTickHandler {

	void handleTick(IFxcmInstrument instrument, ITickPriceCandle candle);

}
