package com.robindrew.trading.fxcm.platform.api.java.command.openposition;

import static com.fxcm.external.api.util.MessageGenerator.generateMarketOrder;

import com.fxcm.fix.ISide;
import com.fxcm.fix.trade.ExecutionReport;
import com.fxcm.fix.trade.OrderSingle;
import com.google.gson.GsonBuilder;
import com.robindrew.trading.fxcm.platform.api.java.FxcmJava;
import com.robindrew.trading.fxcm.platform.api.java.command.FxcmGatewayCommand;
import com.robindrew.trading.fxcm.platform.api.java.gateway.FxcmGateway;
import com.robindrew.trading.position.order.IPositionOrder;

public class OpenPositionCommand extends FxcmGatewayCommand<Boolean> {

	private final IPositionOrder order;

	public OpenPositionCommand(IPositionOrder order) {
		this.order = order;
	}

	@Override
	public Boolean executeCommand(FxcmGateway gateway) {

		String account = gateway.getDefaultAccount();
		double amount = 12500.0d;//order.getTradeSize().doubleValue();
		ISide side = FxcmJava.toSide(order.getDirection());
		String currency = order.getInstrument().getName();
		OrderSingle marketOrder = generateMarketOrder(account, amount, side, currency, "Open new position");

		ExecutionReport response = (ExecutionReport) gateway.execute(marketOrder);

		// TODO: Handle response
		System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(response));
		return true;
	}

}
