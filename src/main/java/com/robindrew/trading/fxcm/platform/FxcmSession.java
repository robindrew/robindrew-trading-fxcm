package com.robindrew.trading.fxcm.platform;

import com.robindrew.common.util.Check;

public class FxcmSession implements IFxcmSession {

	private final FxcmCredentials credentials;
	private final FxcmEnvironment environment;

	public FxcmSession(FxcmCredentials credentials, FxcmEnvironment environment) {
		this.credentials = Check.notNull("credentials", credentials);
		this.environment = Check.notNull("environment", environment);
	}

	@Override
	public FxcmCredentials getCredentials() {
		return credentials;
	}

	@Override
	public FxcmEnvironment getEnvironment() {
		return environment;
	}

}
