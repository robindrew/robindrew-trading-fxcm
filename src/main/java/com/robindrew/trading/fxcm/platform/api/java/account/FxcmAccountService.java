package com.robindrew.trading.fxcm.platform.api.java.account;

import static com.robindrew.common.locale.CurrencyCode.GBP;
import static com.robindrew.common.util.Check.notNull;

import java.util.List;

import com.robindrew.trading.fxcm.platform.api.java.IFxcmJavaService;
import com.robindrew.trading.fxcm.platform.api.java.command.getaccounts.FxcmTradingAccount;
import com.robindrew.trading.provider.ITradingProvider;
import com.robindrew.trading.provider.TradingProvider;
import com.robindrew.trading.trade.money.IMoney;
import com.robindrew.trading.trade.money.Money;

public class FxcmAccountService implements IFxcmAccountService {

	private final IFxcmJavaService java;

	public FxcmAccountService(IFxcmJavaService java) {
		this.java = notNull("java", java);
	}

	@Override
	public ITradingProvider getProvider() {
		return TradingProvider.FXCM;
	}

	@Override
	public String getAccountId() {
		return getAccount().getId();
	}

	private FxcmTradingAccount getAccount() {
		// TODO: How to handle multiple accounts correctly?
		List<FxcmTradingAccount> accounts = java.getAccounts();
		if (accounts.size() != 1) {
			throw new IllegalStateException("Expected one account: " + accounts);
		}
		return accounts.get(0);
	}

	@Override
	public IMoney getBalance() {
		// TODO: Get correct currency code
		return new Money(getAccount().getBalance(), GBP);
	}
}
