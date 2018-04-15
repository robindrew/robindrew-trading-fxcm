package com.robindrew.trading.fxcm.platform.rest;

import java.util.List;

import com.robindrew.common.util.Check;
import com.robindrew.trading.IInstrument;
import com.robindrew.trading.platform.positions.PositionService;
import com.robindrew.trading.position.IPosition;
import com.robindrew.trading.position.closed.IClosedPosition;
import com.robindrew.trading.position.order.IPositionOrder;
import com.robindrew.trading.price.precision.IPricePrecision;
import com.robindrew.trading.trade.funds.AccountFunds;

public class FxcmPositionService extends PositionService {

	private final FxcmRestService rest;

	public FxcmPositionService(FxcmRestService rest) {
		this.rest = Check.notNull("rest", rest);
	}

	@Override
	public List<? extends IPosition> getAllPositions() {
		return rest.getPositions();
	}

	@Override
	public IPricePrecision getPrecision(IInstrument instrument) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IClosedPosition closePosition(IPosition position) {
		throw new UnsupportedOperationException();
	}

	@Override
	public AccountFunds getAvailableFunds() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IPosition openPosition(IPositionOrder order) {
		throw new UnsupportedOperationException();
	}
}
