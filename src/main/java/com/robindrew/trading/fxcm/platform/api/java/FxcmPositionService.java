package com.robindrew.trading.fxcm.platform.api.java;

import static com.robindrew.trading.provider.TradingProvider.FXCM;

import java.util.List;

import com.robindrew.common.util.Check;
import com.robindrew.trading.platform.positions.AbstractPositionService;
import com.robindrew.trading.position.IPosition;

public class FxcmPositionService extends AbstractPositionService {

	private final FxcmJavaService java;

	public FxcmPositionService(FxcmJavaService java) {
		super(FXCM);
		this.java = Check.notNull("java", java);
	}

	@Override
	public List<? extends IPosition> getAllPositions() {
		return java.getPositions();
	}

}
