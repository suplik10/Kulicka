package cz.kulicka.strategy.impl;

import cz.kulicka.entity.Candlestick;
import cz.kulicka.entity.Order;
import cz.kulicka.entity.Ticker;
import cz.kulicka.service.BinanceApiService;
import cz.kulicka.strategy.OrderStrategy;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.List;

public class FirstDumbStrategyImpl implements OrderStrategy {

    static Logger log = Logger.getLogger(FirstDumbStrategyImpl.class);

    private BinanceApiService binanceApiService;

    public FirstDumbStrategyImpl(BinanceApiService binanceApiService) {
        this.binanceApiService = binanceApiService;
    }


    public boolean buy(Ticker ticker, List<Order> activeOrders) {
        boolean createOrder = false;

        List<Candlestick> candlestickList = binanceApiService.getCandlestickBars(ticker.getSymbol(), "5m", 4);
        log.debug("Currency " + ticker.getSymbol());
        for (int y = 0; y < candlestickList.size() - 1; y++) {
            log.debug(new Date(candlestickList.get(y).getOpenTime()) + " open value " + candlestickList.get(y).getOpen() + " closed value: " + candlestickList.get(y).getClose());

            if (Double.parseDouble(candlestickList.get(y).getClose()) > Double.parseDouble(candlestickList.get(y).getOpen())) {
                createOrder = true;
            } else {
                createOrder = false;
                break;
            }
        }

        return createOrder;
    }

    @Override
    public boolean buy(Ticker ticker, List<Order> activeOrders, double actualBTCUSDT) {
        return false;
    }

    @Override
    public boolean sell(Order order, double actualSellPriceForOrderWithFee) {
        if (order.getRiskValue() > 1) {
            order.setRiskValue(order.getRiskValue() - 1);
            log.info("Order resuming id: " + order.getId());
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean instaSellForProfit(Order order, double actualSellPriceForOrderWithFee) {
        return false;
    }


}
