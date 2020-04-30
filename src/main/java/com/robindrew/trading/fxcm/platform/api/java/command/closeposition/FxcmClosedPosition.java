package com.robindrew.trading.fxcm.platform.api.java.command.closeposition;

import static com.robindrew.trading.fxcm.platform.api.java.FxcmJava.toBigDecimal;
import static com.robindrew.trading.fxcm.platform.api.java.FxcmJava.toLocalDateTime;

import com.fxcm.fix.trade.ExecutionReport;
import com.robindrew.trading.fxcm.platform.api.java.command.getopenpositions.FxcmPosition;
import com.robindrew.trading.position.closed.ClosedPosition;

public class FxcmClosedPosition extends ClosedPosition {

	public FxcmClosedPosition(FxcmPosition position, ExecutionReport report) {
		super(position, toLocalDateTime(report.getTransactTime()), toBigDecimal(report.getPrice()));
	}

}
