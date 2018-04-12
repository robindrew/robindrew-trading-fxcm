package com.robindrew.trading.fxcm.platform;

public interface IFxcmSession {

	FxcmCredentials getCredentials();

	FxcmEnvironment getEnvironment();

}