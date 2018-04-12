package com.robindrew.trading.fxcm.platform;

import com.robindrew.common.util.Check;

public class FxcmCredentials {

	private final String username;
	private final String password;

	public FxcmCredentials(String username, String password) {
		this.username = Check.notEmpty("username", username);
		this.password = Check.notEmpty("password", password);
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	@Override
	public String toString() {
		return username;
	}

}
