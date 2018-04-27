package com.robindrew.trading.fxcm.platform.fix.getaccounts;

import static com.robindrew.trading.fxcm.platform.fix.FxcmFix.toBigDecimal;

import java.math.BigDecimal;

import com.fxcm.fix.posttrade.CollateralReport;
import com.robindrew.common.text.Strings;

public class FxcmTradingAccount {

	private final String id;
	private final BigDecimal balance;

	public FxcmTradingAccount(CollateralReport report) {
		this.id = report.getAccount();
		this.balance = toBigDecimal(report.getEndCash());
	}

	public String getId() {
		return id;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	@Override
	public String toString() {
		return Strings.toString(this);
	}
}
