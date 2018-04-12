package com.robindrew.trading.fxcm;

import static com.robindrew.trading.fxcm.platform.FxcmEnvironment.DEMO;

import org.junit.Test;

import com.fxcm.fix.pretrade.TradingSessionStatus;
import com.robindrew.common.util.Threads;
import com.robindrew.trading.fxcm.platform.FxcmCredentials;
import com.robindrew.trading.fxcm.platform.FxcmEnvironment;
import com.robindrew.trading.fxcm.platform.FxcmSession;
import com.robindrew.trading.fxcm.platform.rest.FxcmRestService;

public class FxcmConnectionTest {

	@Test
	public void connect() throws Exception {

		String username = System.getProperty("username");
		String password = System.getProperty("password");

		FxcmCredentials credentials = new FxcmCredentials(username, password);
		FxcmEnvironment environment = DEMO;
		FxcmSession session = new FxcmSession(credentials, environment);
		FxcmRestService rest = new FxcmRestService(session);
		rest.login();
		
		TradingSessionStatus status = rest.getTradingSessionStatus().get();
//		MarketDataSnapshot snapshot = rest.getMarketDataSnapshot(status);
//		System.out.println(snapshot);
		
		Threads.sleepForever();
		
	}
}
