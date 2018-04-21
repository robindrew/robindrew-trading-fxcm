package com.robindrew.trading.fxcm.platform.fix.openposition;

import com.fxcm.external.api.util.MessageGenerator;
import com.fxcm.fix.ISide;
import com.fxcm.fix.trade.OrderSingle;
import com.fxcm.messaging.ITransportable;
import com.robindrew.trading.fxcm.platform.fix.FxcmGateway;
import com.robindrew.trading.fxcm.platform.fix.FxcmGatewayCommand;
import com.robindrew.trading.fxcm.platform.fix.FxcmFix;
import com.robindrew.trading.position.order.IPositionOrder;

public class OpenPositionCommand extends FxcmGatewayCommand<Boolean> {

	private final IPositionOrder order;

	public OpenPositionCommand(IPositionOrder order) {
		this.order = order;
	}

	@Override
	public Boolean executeCommand(FxcmGateway gateway) {

		String account = gateway.getDefaultAccount();
		double amount = order.getTradeSize().doubleValue();
		ISide side = FxcmFix.toSide(order.getDirection());
		String currency = order.getInstrument().getName();
		OrderSingle open = MessageGenerator.generateMarketOrder(account, amount, side, currency, "Open new position");

		ITransportable response = gateway.execute(open);

		// TODO: Handle response
		System.out.println(response);
		return true;
	}

}
