package cz.kulicka.strategy.impl;

import cz.kulicka.PropertyPlaceholder;
import cz.kulicka.entity.Order;
import cz.kulicka.entity.Ticker;
import cz.kulicka.service.BinanceApiServiceMKA;
import cz.kulicka.service.MacdIndicatorService;
import cz.kulicka.service.OrderService;
import cz.kulicka.strategy.OrderStrategy;

public class MacdStrategyImpl extends AbstractStrategy implements OrderStrategy {

	protected MacdStrategyImpl(BinanceApiServiceMKA binanceApiServiceMKA, MacdIndicatorService macdIndicatorService, OrderService orderService, PropertyPlaceholder propertyPlaceholder) {
		super(binanceApiServiceMKA, macdIndicatorService, orderService, propertyPlaceholder);
	}

	@Override
	public boolean buy(Ticker ticker, double actualBTCUSDT) {
		return false;
	}

	@Override
	public boolean rebuyStopLossProtection(Ticker ticker, double actualBTCUSDT) {
		return false;
	}

	@Override
	public boolean sell(Order order, double actualBTCUSDT, double lastPriceBTC) {
		return false;
	}

	@Override
	public boolean closeNonActiveOpenOrder(Order order) {
		return false;
	}

	@Override
	public boolean instaSell(Order order, double actualBTCUSDT, double lastPriceBTC) {
		return false;
	}

	@Override
	public void panicSellAll() {

	}
}

