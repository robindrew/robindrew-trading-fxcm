package com.robindrew.trading.fxcm.platform.api.java.command.openposition;

import static com.fxcm.external.api.util.MessageGenerator.generateMarketOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fxcm.fix.ISide;
import com.fxcm.fix.trade.ExecutionReport;
import com.fxcm.fix.trade.OrderSingle;
import com.robindrew.common.text.Strings;
import com.robindrew.trading.fxcm.platform.api.java.FxcmJava;
import com.robindrew.trading.fxcm.platform.api.java.command.FxcmGatewayCommand;
import com.robindrew.trading.fxcm.platform.api.java.command.getopenpositions.FxcmPosition;
import com.robindrew.trading.fxcm.platform.api.java.gateway.FxcmGateway;
import com.robindrew.trading.position.order.IPositionOrder;

public class OpenPositionCommand extends FxcmGatewayCommand<FxcmPosition> {

	private static final Logger log = LoggerFactory.getLogger(OpenPositionCommand.class);
	
	private final IPositionOrder order;

	public OpenPositionCommand(IPositionOrder order) {
		this.order = order;
	}

	@Override
	public FxcmPosition executeCommand(FxcmGateway gateway) {

		String account = gateway.getDefaultAccount();
		double amount = order.getTradeSize().doubleValue();
		ISide side = FxcmJava.toSide(order.getDirection());
		String currency = order.getInstrument().getName();

		OrderSingle request = generateMarketOrder(account, amount, side, currency, "");
		log.info("[OpenPosition Request] {}", Strings.json(request, true));
		ExecutionReport response = (ExecutionReport) gateway.execute(request);
		log.info("[OpenPosition Response] {}", Strings.json(response, true));
		return new FxcmPosition(response);
	}

}
