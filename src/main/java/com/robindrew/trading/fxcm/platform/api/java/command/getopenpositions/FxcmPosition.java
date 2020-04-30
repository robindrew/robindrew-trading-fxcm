package com.robindrew.trading.fxcm.platform.api.java.command.getopenpositions;

import static com.robindrew.trading.fxcm.platform.api.java.FxcmJava.toBigDecimal;
import static com.robindrew.trading.fxcm.platform.api.java.FxcmJava.toDirection;
import static com.robindrew.trading.fxcm.platform.api.java.FxcmJava.toFxcmInstrument;
import static com.robindrew.trading.fxcm.platform.api.java.FxcmJava.toLocalDateTime;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fxcm.fix.posttrade.PositionReport;
import com.fxcm.fix.trade.ExecutionReport;
import com.robindrew.common.locale.CurrencyCode;
import com.robindrew.common.text.Strings;
import com.robindrew.trading.fxcm.IFxcmInstrument;
import com.robindrew.trading.position.IPosition;
import com.robindrew.trading.trade.TradeDirection;

public class FxcmPosition implements IPosition {

	private final String id;
	private final String account;
	private final CurrencyCode currency;
	private final LocalDateTime openDate;
	private final IFxcmInstrument instrument;
	private final BigDecimal openPrice;
	private final BigDecimal tradeSize;
	private final TradeDirection direction;

	public FxcmPosition(PositionReport report) {
		this.id = report.getFXCMPosID();
		this.account = report.getAccount();
		this.currency = CurrencyCode.valueOf(report.getCurrency());
		this.openDate = toLocalDateTime(report.getFXCMPosOpenTime());
		this.instrument = toFxcmInstrument(report.getInstrument());
		this.openPrice = toBigDecimal(report.getSettlPrice());
		this.tradeSize = toBigDecimal(report.getPositionQty().getQty());
		this.direction = toDirection(report.getPositionQty().getSide());
		// TODO: Parse stop and limit if available?
	}

	public FxcmPosition(ExecutionReport report) {
		this.id = report.getFXCMPosID();
		this.account = report.getAccount();
		this.currency = CurrencyCode.valueOf(report.getCurrency());
		this.openDate = toLocalDateTime(report.getTransactTime());
		this.instrument = toFxcmInstrument(report.getInstrument());
		this.openPrice = toBigDecimal(report.getPrice());
		this.tradeSize = toBigDecimal(report.getOrderQty());
		this.direction = toDirection(report.getSide());
		// TODO: Parse stop and limit if available?
	}

	public String getAccount() {
		return account;
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
	public IFxcmInstrument getInstrument() {
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

	@Override
	public String toString() {
		return Strings.toString(this);
	}

}
