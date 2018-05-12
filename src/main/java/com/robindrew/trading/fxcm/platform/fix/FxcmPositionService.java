package com.robindrew.trading.fxcm.platform.fix;

import static com.robindrew.trading.provider.TradingProvider.FXCM;

import java.util.List;

import com.robindrew.common.util.Check;
import com.robindrew.trading.platform.positions.AbstractPositionService;
import com.robindrew.trading.position.IPosition;

public class FxcmPositionService extends AbstractPositionService {

	private final FxcmFixService fix;

	public FxcmPositionService(FxcmFixService fix) {
		super(FXCM);
		this.fix = Check.notNull("fix", fix);
	}

	@Override
	public List<? extends IPosition> getAllPositions() {
		return fix.getPositions();
	}

}
