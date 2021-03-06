package com.robindrew.trading.fxcm.platform.api.java.command.getaccounts;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fxcm.fix.posttrade.CollateralInquiry;
import com.fxcm.fix.posttrade.CollateralReport;
import com.robindrew.trading.fxcm.platform.api.java.command.FxcmGatewayCommand;
import com.robindrew.trading.fxcm.platform.api.java.command.response.GatewayResponseCache.GatewayResponse;
import com.robindrew.trading.fxcm.platform.api.java.gateway.FxcmGateway;

public class GetAccountsCommand extends FxcmGatewayCommand<List<FxcmTradingAccount>> {

	private static final Logger log = LoggerFactory.getLogger(GetAccountsCommand.class);

	@Override
	public List<FxcmTradingAccount> executeCommand(FxcmGateway gateway) throws Exception {

		String requestId = gateway.sendMessage(new CollateralInquiry());
		try (GatewayResponse response = gateway.getResponseCache().get(requestId)) {

			// Wait for the accounts
			GetAccountsResponse accounts = response.populate(new GetAccountsResponse());

			List<FxcmTradingAccount> list = new ArrayList<>();
			for (CollateralReport report : accounts.getReportList()) {
				FxcmTradingAccount account = new FxcmTradingAccount(report);
				log.info("{}", account);
				list.add(account);
			}

			// Set the default account (if only one is available)
			if (list.size() == 1) {
				gateway.setDefaultAccount(list.get(0));
			}

			return list;
		}
	}

}
