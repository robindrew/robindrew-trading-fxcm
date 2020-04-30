package com.robindrew.trading.fxcm.platform.api.java.command.closeposition;

import static com.fxcm.external.api.util.MessageGenerator.generateCloseMarketOrder;

import com.fxcm.fix.ISide;
import com.fxcm.fix.trade.ExecutionReport;
import com.fxcm.fix.trade.OrderSingle;
import com.robindrew.common.text.Strings;
import com.robindrew.trading.fxcm.platform.api.java.FxcmJava;
import com.robindrew.trading.fxcm.platform.api.java.command.FxcmGatewayCommand;
import com.robindrew.trading.fxcm.platform.api.java.command.getopenpositions.FxcmPosition;
import com.robindrew.trading.fxcm.platform.api.java.gateway.FxcmGateway;

public class ClosePositionCommand extends FxcmGatewayCommand<FxcmClosedPosition> {

	private final FxcmPosition position;

	public ClosePositionCommand(FxcmPosition position) {
		this.position = position;
	}

	@Override
	public FxcmClosedPosition executeCommand(FxcmGateway gateway) {

		String id = position.getId();
		String accountId = position.getAccount();
		double quantity = position.getTradeSize().doubleValue();
		ISide side = FxcmJava.toSide(position.getDirection().invert());
		String symbol = position.getInstrument().getName();

		// Execute
		OrderSingle close = generateCloseMarketOrder(id, accountId, quantity, side, symbol, "Close position " + id);
		ExecutionReport response = gateway.execute(close);
		return new FxcmClosedPosition(position, response);
	}

}
