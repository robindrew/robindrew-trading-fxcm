package com.robindrew.trading.fxcm.platform.rest.getaccounts;

import static com.robindrew.trading.fxcm.platform.rest.FxcmRest.toBigDecimal;

import java.math.BigDecimal;

import com.fxcm.fix.posttrade.CollateralReport;
import com.robindrew.common.text.Strings;
import com.robindrew.trading.account.ITradingAccount;

public class TradingAccountHolder implements ITradingAccount {

	private final String id;
	private final BigDecimal balance;

	public TradingAccountHolder(CollateralReport report) {
		this.id = report.getAccount();
		this.balance = toBigDecimal(report.getEndCash());
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public BigDecimal getBalance() {
		return balance;
	}

	@Override
	public String toString() {
		return Strings.toString(this);
	}
}
