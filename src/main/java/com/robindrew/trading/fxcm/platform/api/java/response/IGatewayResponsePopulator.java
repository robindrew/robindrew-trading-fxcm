package com.robindrew.trading.fxcm.platform.api.java.response;

import java.util.List;

import com.fxcm.messaging.ITransportable;

public interface IGatewayResponsePopulator {

	boolean populate(List<ITransportable> list);

}
