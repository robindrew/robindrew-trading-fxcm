package com.robindrew.trading.fxcm.platform.api.java.command.getopenpositions;

import java.util.ArrayList;
import java.util.List;

import com.fxcm.fix.posttrade.PositionReport;
import com.fxcm.fix.posttrade.RequestForPositionsAck;
import com.fxcm.messaging.ITransportable;
import com.google.common.collect.ImmutableList;
import com.robindrew.trading.fxcm.platform.api.java.command.response.IGatewayResponsePopulator;

public class GetOpenPositionsResponse implements IGatewayResponsePopulator {

	private RequestForPositionsAck ack;
	private List<PositionReport> reportList = new ArrayList<>();

	public RequestForPositionsAck getAck() {
		return ack;
	}

	public List<PositionReport> getReportList() {
		return ImmutableList.copyOf(reportList);
	}

	@Override
	public boolean populate(List<ITransportable> list) {

		// Ready to be populated?
		RequestForPositionsAck ack = (RequestForPositionsAck) list.remove(0);
		int count = ack.getTotalNumPosReports();
		if (count != list.size()) {
			return false;
		}

		// Populate!
		this.ack = ack;
		for (ITransportable element : list) {
			this.reportList.add((PositionReport) element);
		}

		return true;
	}

}
