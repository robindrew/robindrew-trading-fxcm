package com.robindrew.trading.fxcm.platform.rest.getpositions;

import static com.robindrew.trading.fxcm.platform.rest.FxcmRest.toDirection;
import static com.robindrew.trading.fxcm.platform.rest.FxcmRest.toFxcmInstrument;
import static com.robindrew.trading.fxcm.platform.rest.FxcmRest.toLocalDateTime;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fxcm.fix.posttrade.PositionReport;
import com.robindrew.common.locale.CurrencyCode;
import com.robindrew.trading.fxcm.FxcmInstrument;
import com.robindrew.trading.position.IPosition;
import com.robindrew.trading.trade.TradeDirection;

public class PositionReportHolder implements IPosition {

	private final String id;
	private final CurrencyCode currency;
	private final LocalDateTime openDate;
	private final FxcmInstrument instrument;
	private final BigDecimal openPrice;
	private final BigDecimal tradeSize;
	private final TradeDirection direction;

	public PositionReportHolder(PositionReport report) {
		this.id = report.getOrderID();
		this.currency = CurrencyCode.valueOf(report.getCurrency());
		this.openDate = toLocalDateTime(report.getFXCMPosOpenTime());
		this.instrument = toFxcmInstrument(report.getInstrument());
		this.openPrice = new BigDecimal(report.getSettlPrice());
		this.tradeSize = new BigDecimal(report.getPositionQty().getQty());
		this.direction = toDirection(report.getPositionQty().getSide());
		// TODO: Parse stop and limit if available?
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public TradeDirection getDirection() {
		return direction;
	}

	@Override
	public LocalDateTime getOpenDate() {
		return openDate;
	}

	@Override
	public CurrencyCode getTradeCurrency() {
		return currency;
	}

	@Override
	public BigDecimal getOpenPrice() {
		return openPrice;
	}

	@Override
	public BigDecimal getTradeSize() {
		return tradeSize;
	}

	@Override
	public FxcmInstrument getInstrument() {
		return instrument;
	}

	@Override
	public BigDecimal getProfitLimitPrice() {
		return null;
	}

	@Override
	public BigDecimal getStopLossPrice() {
		return null;
	}

}
