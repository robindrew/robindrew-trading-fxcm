package com.robindrew.trading.fxcm.platform.api.java.position;

import static com.robindrew.trading.provider.TradingProvider.FXCM;

import java.util.List;

import com.robindrew.common.util.Check;
import com.robindrew.trading.fxcm.platform.api.java.FxcmJavaService;
import com.robindrew.trading.fxcm.platform.api.java.command.getopenpositions.FxcmPosition;
import com.robindrew.trading.platform.positions.AbstractPositionService;
import com.robindrew.trading.position.IPosition;
import com.robindrew.trading.position.closed.IClosedPosition;
import com.robindrew.trading.position.order.IPositionOrder;

public class FxcmPositionService extends AbstractPositionService implements IFxcmPositionService {

	private final FxcmJavaService java;

	public FxcmPositionService(FxcmJavaService java) {
		super(FXCM);
		this.java = Check.notNull("java", java);
	}

	@Override
	public List<? extends IPosition> getAllPositions() {
		return java.getPositions();
	}

	@Override
	public IPosition openPosition(IPositionOrder order) {
		return java.openPosition(order);
	}

	@Override
	public IClosedPosition closePosition(IPosition position) {
		return java.closePosition((FxcmPosition) position);
	}
}
