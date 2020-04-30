package com.robindrew.trading.fxcm.platform;

public class FxcmException extends RuntimeException {

	private static final long serialVersionUID = 7702996549267961390L;

	public FxcmException(Throwable cause) {
		super(cause);
	}

	public FxcmException(String message, Throwable cause) {
		super(message, cause);
	}

	public FxcmException(String message) {
		super(message);
	}

}
