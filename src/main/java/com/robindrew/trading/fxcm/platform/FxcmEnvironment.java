package com.robindrew.trading.fxcm.platform;

public enum FxcmEnvironment {

	/** Prod Environment. */
	PROD("Real", "http://www.fxcorporate.com/Hosts.jsp"),
	/** Demo Environment. */
	DEMO("Demo", "http://www.fxcorporate.com/Hosts.jsp");

	private final String station;
	private final String server;

	private FxcmEnvironment(String station, String server) {
		this.station = station;
		this.server = server;
	}

	public String getStation() {
		return station;
	}

	public String getServer() {
		return server;
	}

}
