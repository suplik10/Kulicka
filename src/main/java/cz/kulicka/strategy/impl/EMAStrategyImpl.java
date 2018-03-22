package cz.kulicka.strategy.impl;

import cz.kulicka.PropertyPlaceholder;
import cz.kulicka.entity.Order;
import cz.kulicka.entity.Ticker;
import cz.kulicka.service.BinanceApiService;
import cz.kulicka.service.MacdIndicatorService;
import cz.kulicka.service.OrderService;
import cz.kulicka.strategy.OrderStrategy;
import org.apache.log4j.Logger;

public class EMAStrategyImpl implements OrderStrategy {

    static Logger log = Logger.getLogger(EMAStrategyImpl.class);

    private BinanceApiService binanceApiService;
    private MacdIndicatorService macdIndicatorService;
    private PropertyPlaceholder propertyPlaceholder;
    private OrderService orderService;

    public EMAStrategyImpl(BinanceApiService binanceApiService, MacdIndicatorService macdIndicatorService, OrderService orderService, PropertyPlaceholder propertyPlaceholder) {
        this.binanceApiService = binanceApiService;
        this.macdIndicatorService = macdIndicatorService;
        this.orderService = orderService;
        this.propertyPlaceholder = propertyPlaceholder;
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
}
