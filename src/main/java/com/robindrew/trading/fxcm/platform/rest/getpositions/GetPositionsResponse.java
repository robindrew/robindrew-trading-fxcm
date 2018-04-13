package com.robindrew.trading.fxcm.platform.rest.getpositions;

import java.util.ArrayList;
import java.util.List;

import com.fxcm.fix.posttrade.PositionReport;
import com.fxcm.fix.posttrade.RequestForPositionsAck;
import com.google.common.collect.ImmutableList;
import com.robindrew.trading.fxcm.platform.rest.response.IResponseList;

public class GetPositionsResponse implements IResponseList {

	private final RequestForPositionsAck ack;
	private final List<PositionReport> reportList;

	public GetPositionsResponse(RequestForPositionsAck ack) {
		this.ack = ack;
		this.reportList = new ArrayList<>(ack.getTotalNumPosReports());
	}

	public RequestForPositionsAck getAck() {
		return ack;
	}

	public List<PositionReport> getReportList() {
		return ImmutableList.copyOf(reportList);
	}

	@Override
	public boolean isReady() {
		return reportList.size() == ack.getTotalNumPosReports();
	}

	@Override
	public void addResponse(Object response) {
		reportList.add((PositionReport) response);
	}
}
