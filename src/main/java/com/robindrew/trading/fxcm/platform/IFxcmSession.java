package com.robindrew.trading.fxcm.platform;

public interface IFxcmSession {

	IFxcmCredentials getCredentials();

	FxcmEnvironment getEnvironment();

}