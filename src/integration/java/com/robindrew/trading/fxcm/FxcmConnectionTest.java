package com.robindrew.trading.fxcm;

import static com.robindrew.trading.fxcm.platform.FxcmEnvironment.DEMO;
import static java.util.concurrent.TimeUnit.SECONDS;

import org.junit.Test;

import com.robindrew.common.util.Threads;
import com.robindrew.trading.fxcm.platform.FxcmCredentials;
import com.robindrew.trading.fxcm.platform.FxcmEnvironment;
import com.robindrew.trading.fxcm.platform.FxcmSession;
import com.robindrew.trading.fxcm.platform.rest.FxcmRestService;
import com.robindrew.trading.fxcm.platform.rest.getopenpositions.FxcmPosition;

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

		for (FxcmPosition position : rest.getPositions()) {
			rest.closePosition(position);
			break;
		}

		Threads.sleep(10, SECONDS);
	}
}
