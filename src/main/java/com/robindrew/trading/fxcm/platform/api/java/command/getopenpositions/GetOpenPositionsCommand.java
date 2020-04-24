package com.robindrew.trading.fxcm.platform.api.java.command.getopenpositions;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fxcm.fix.posttrade.PositionReport;
import com.robindrew.trading.fxcm.platform.api.java.command.FxcmGatewayCommand;
import com.robindrew.trading.fxcm.platform.api.java.command.response.GatewayResponseCache.GatewayResponse;
import com.robindrew.trading.fxcm.platform.api.java.gateway.FxcmGateway;

public class GetOpenPositionsCommand extends FxcmGatewayCommand<List<FxcmPosition>> {

	private static final Logger log = LoggerFactory.getLogger(GetOpenPositionsCommand.class);

	@Override
	public List<FxcmPosition> executeCommand(FxcmGateway gateway) {

		String requestId = gateway.getGateway().requestOpenPositions();
		try (GatewayResponse response = gateway.getResponseCache().get(requestId)) {

			// Wait for the positions
			GetOpenPositionsResponse positions = response.populate(new GetOpenPositionsResponse());

			List<FxcmPosition> list = new ArrayList<>();
			for (PositionReport report : positions.getReportList()) {

				FxcmPosition position = new FxcmPosition(report);
				log.info("{}", position);
				list.add(position);
			}

			return list;
		}
	}
}
