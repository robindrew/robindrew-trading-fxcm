package com.robindrew.trading.fxcm.platform.rest.response;

import java.util.List;

import com.fxcm.messaging.ITransportable;

public interface IGatewayResponsePopulator {

	boolean populate(List<ITransportable> list);

}
