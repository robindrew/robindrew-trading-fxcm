package com.robindrew.trading.fxcm.platform.rest.response;

public interface IResponseList {

	boolean isReady();

	void addResponse(Object response);
}
