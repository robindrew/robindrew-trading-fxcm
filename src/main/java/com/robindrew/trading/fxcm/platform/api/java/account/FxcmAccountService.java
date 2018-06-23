package com.robindrew.trading.fxcm.platform.api.java.account;

import static com.robindrew.common.util.Check.notNull;

import com.robindrew.trading.fxcm.platform.api.java.IFxcmJavaService;
import com.robindrew.trading.provider.ITradingProvider;
import com.robindrew.trading.provider.TradingProvider;
import com.robindrew.trading.trade.cash.ICash;

public class FxcmAccountService implements IFxcmAccountService {

	private final IFxcmJavaService java;

	public FxcmAccountService(IFxcmJavaService java) {
		this.java = notNull("java", java);;
	}

	@Override
	public ITradingProvider getProvider() {
		return TradingProvider.FXCM;
	}

	@Override
	public String getAccountId() {
		return null;
	}

	@Override
	public ICash getBalance() {
		return null;
	}
}