package com.robindrew.trading.fxcm.platform.api.java.command.response;

import java.util.List;

import com.fxcm.messaging.ITransportable;

public interface IGatewayResponsePopulator {

	boolean populate(List<ITransportable> list);

}
