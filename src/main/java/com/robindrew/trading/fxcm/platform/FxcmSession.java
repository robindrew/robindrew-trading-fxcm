package com.robindrew.trading.fxcm.platform;

import com.robindrew.common.util.Check;

public class FxcmSession implements IFxcmSession {

	private final IFxcmCredentials credentials;
	private final FxcmEnvironment environment;

	public FxcmSession(IFxcmCredentials credentials, FxcmEnvironment environment) {
		this.credentials = Check.notNull("credentials", credentials);
		this.environment = Check.notNull("environment", environment);
	}

	@Override
	public IFxcmCredentials getCredentials() {
		return credentials;
	}

	@Override
	public FxcmEnvironment getEnvironment() {
		return environment;
	}

}
