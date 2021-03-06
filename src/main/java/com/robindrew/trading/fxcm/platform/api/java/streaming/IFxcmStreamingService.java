package com.robindrew.trading.fxcm.platform.api.java.streaming;

import com.robindrew.trading.fxcm.IFxcmInstrument;
import com.robindrew.trading.fxcm.platform.api.java.gateway.IFxcmGatewayTickHandler;
import com.robindrew.trading.platform.streaming.IStreamingService;

public interface IFxcmStreamingService extends IStreamingService<IFxcmInstrument>, IFxcmGatewayTickHandler {

}
