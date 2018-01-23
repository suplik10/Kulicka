package cz.kulicka.strategy.impl;

import cz.kulicka.entity.Candlestick;
import cz.kulicka.entity.Order;
import cz.kulicka.entity.Ticker;
import cz.kulicka.enums.CandlestickInterval;
import cz.kulicka.services.BinanceApiService;
import cz.kulicka.strategy.OrderStrategy;
import cz.kulicka.utils.MathUtil;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.List;

public class SecondDumbStrategyImpl implements OrderStrategy {

    static Logger log = Logger.getLogger(SecondDumbStrategyImpl.class);

    private BinanceApiService binanceApiService;

    public SecondDumbStrategyImpl(BinanceApiService binanceApiService) {
        this.binanceApiService = binanceApiService;
    }

    @Override
    public boolean buy(Ticker ticker, List<Order> activeOrders) {
        boolean createOrder = false;

        for (Order order : activeOrders) {
            if (ticker.getSymbol().equals(order.getSymbol())) {
                return false;
            }
        }

        List<Candlestick> candlestickList = binanceApiService.getCandlestickBars(ticker.getSymbol(), CandlestickInterval.ONE_MINUTE, 4);
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
    public boolean sell(Order order) {
        double actualPrice = Double.parseDouble(binanceApiService.getLastPrice(order.getSymbol()).getPrice());

        if (MathUtil.getPercentageProfit(order.getStepedPrice(), actualPrice) > 0.9) {
            //HODL!!!
            //order.setStepedPrice(actualPrice);
            return true;
        } else if (MathUtil.getPercentageProfit(order.getStepedPrice(), actualPrice) < -2) {
            return true;
        } else {
            order.setStepedPrice(actualPrice);
            return false;
        }
    }
}
