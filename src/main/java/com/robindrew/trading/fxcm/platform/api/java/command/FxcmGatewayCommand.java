package com.robindrew.trading.fxcm.platform.api.java.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.robindrew.trading.fxcm.platform.api.java.gateway.FxcmGateway;
import com.robindrew.trading.platform.TradingPlatformException;

public abstract class FxcmGatewayCommand<R> {

	private static final Logger log = LoggerFactory.getLogger(FxcmGatewayCommand.class);

	public String getName() {
		return getClass().getSimpleName();
	}

	public R execute(FxcmGateway gateway) {
		try {
			log.info("[Executing] {}", getName());
			Stopwatch timer = Stopwatch.createStarted();

			R result = executeCommand(gateway);

			timer.stop();
			log.info("[Executed] {} in {}", getName(), timer);
			return result;

		} catch (Exception e) {
			throw new TradingPlatformException(getName() + " command failed", e);
		}
	}

	protected abstract R executeCommand(FxcmGateway gateway) throws Exception;

}
