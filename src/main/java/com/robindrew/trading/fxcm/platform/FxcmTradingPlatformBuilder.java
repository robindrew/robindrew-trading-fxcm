package com.robindrew.trading.fxcm.platform;

import static com.robindrew.common.test.UnitTests.getProperty;
import static com.robindrew.trading.fxcm.platform.FxcmEnvironment.DEMO;

import com.google.common.base.Supplier;
import com.robindrew.common.util.Check;
import com.robindrew.trading.fxcm.platform.api.java.FxcmJavaService;
import com.robindrew.trading.fxcm.platform.api.java.gateway.FxcmGateway;
import com.robindrew.trading.log.ITransactionLog;
import com.robindrew.trading.log.StubTransactionLog;

public class FxcmTradingPlatformBuilder implements Supplier<FxcmTradingPlatform> {

	private String username;
	private String password;
	private FxcmEnvironment environment = DEMO;
	private ITransactionLog transactionLog = new StubTransactionLog();

	public void username(String username) {
		this.username = Check.notEmpty("username", username);
	}

	public void password(String password) {
		this.password = Check.notEmpty("password", password);
	}

	public void environment(FxcmEnvironment environment) {
		this.environment = Check.notNull("environment", environment);
	}

	public void transactionLog(ITransactionLog log) {
		this.transactionLog = Check.notNull("log", log);
	}

	@Override
	public FxcmTradingPlatform get() {

		// Credentials
		String user = username != null ? username : getProperty("fxcm.username");
		String pass = password != null ? password : getProperty("fxcm.password");
		FxcmCredentials credentials = new FxcmCredentials(user, pass);

		FxcmSession session = new FxcmSession(credentials, environment);
		FxcmGateway gateway = new FxcmGateway(transactionLog);
		FxcmJavaService service = new FxcmJavaService(session, gateway, transactionLog);
		service.login();

		return new FxcmTradingPlatform(service);
	}

}
