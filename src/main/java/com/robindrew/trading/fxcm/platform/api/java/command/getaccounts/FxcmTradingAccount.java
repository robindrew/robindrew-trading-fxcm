package com.robindrew.trading.fxcm.platform.api.java.command.getaccounts;

import static com.robindrew.trading.fxcm.platform.api.java.FxcmJava.toBigDecimal;

import java.math.BigDecimal;

import com.fxcm.fix.posttrade.CollateralReport;
import com.robindrew.common.text.Strings;

public class FxcmTradingAccount {

	private final String id;
	private final BigDecimal balance;
	private final BigDecimal minQuantity;

	public FxcmTradingAccount(CollateralReport report) {
		this.id = report.getAccount();
		this.balance = toBigDecimal(report.getEndCash());
		this.minQuantity = toBigDecimal(report.getQuantity());
	}

	public String getId() {
		return id;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public BigDecimal getMinimumQuantity() {
		return minQuantity;
	}

	@Override
	public String toString() {
		return Strings.toString(this);
	}
}
