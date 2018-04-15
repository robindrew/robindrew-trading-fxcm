package com.robindrew.trading.fxcm.platform.rest.response;

import static com.robindrew.common.util.Check.notNull;

import java.util.List;

import com.fxcm.messaging.ITransportable;
import com.robindrew.trading.fxcm.platform.rest.response.GatewayResponseCache.GatewayResponse;

public abstract class ResponseFuturePopulator {

	private final GatewayResponse future;

	public ResponseFuturePopulator(GatewayResponse future) {
		this.future = notNull("future", future);
	}

	public void populate() {
		while (true) {
			List<ITransportable> list = future.get();
		}
	}

	public abstract boolean populate(List<ITransportable> list);

}
