package com.robindrew.trading.fxcm.platform.rest.closeposition;

import static com.fxcm.external.api.util.MessageGenerator.generateCloseMarketOrder;

import com.fxcm.fix.ISide;
import com.fxcm.fix.trade.OrderSingle;
import com.fxcm.messaging.ITransportable;
import com.robindrew.trading.fxcm.platform.rest.FxcmGateway;
import com.robindrew.trading.fxcm.platform.rest.FxcmGatewayCommand;
import com.robindrew.trading.fxcm.platform.rest.FxcmRest;
import com.robindrew.trading.fxcm.platform.rest.getopenpositions.FxcmPosition;

public class ClosePositionCommand extends FxcmGatewayCommand<Boolean> {

	private final FxcmPosition position;

	public ClosePositionCommand(FxcmPosition position) {
		this.position = position;
	}

	@Override
	public Boolean executeCommand(FxcmGateway gateway) {

		String id = position.getId();
		String accountId = position.getAccount();
		double quantity = position.getTradeSize().doubleValue();
		ISide side = FxcmRest.toSide(position.getDirection().invert());
		String symbol = position.getInstrument().getName();
		OrderSingle close = generateCloseMarketOrder(id, accountId, quantity, side, symbol, "Completely close position");

		ITransportable response = gateway.execute(close);

		// TODO: Handle response
		System.out.println(response);
		return true;
	}

}
