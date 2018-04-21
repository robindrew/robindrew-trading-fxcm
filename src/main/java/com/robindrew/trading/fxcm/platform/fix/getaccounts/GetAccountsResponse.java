package com.robindrew.trading.fxcm.platform.fix.getaccounts;

import java.util.ArrayList;
import java.util.List;

import com.fxcm.fix.posttrade.CollateralInquiryAck;
import com.fxcm.fix.posttrade.CollateralReport;
import com.fxcm.messaging.ITransportable;
import com.google.common.collect.ImmutableList;
import com.robindrew.trading.fxcm.platform.fix.response.IGatewayResponsePopulator;

public class GetAccountsResponse implements IGatewayResponsePopulator {

	private CollateralInquiryAck ack;
	private List<CollateralReport> reportList = new ArrayList<>();

	public CollateralInquiryAck getAck() {
		return ack;
	}

	public List<CollateralReport> getReportList() {
		return ImmutableList.copyOf(reportList);
	}

	@Override
	public boolean populate(List<ITransportable> list) {

		// Ready to be populated?
		CollateralInquiryAck ack = (CollateralInquiryAck) list.remove(0);
		int count = ack.getTotNumReports();
		if (count != list.size()) {
			return false;
		}

		// Populate!
		this.ack = ack;
		for (ITransportable element : list) {
			this.reportList.add((CollateralReport) element);
		}

		return true;
	}

}
