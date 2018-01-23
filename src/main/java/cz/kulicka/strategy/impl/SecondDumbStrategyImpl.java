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
    public boolean buy(Ticker ticker) {
        boolean createOrder = false;

        List<Candlestick> candlestickList = binanceApiService.getCandlestickBars(ticker.getSymbol(), CandlestickInterval.FIVE_MINUTES, 4);
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

        if (MathUtil.getPercentageProfit(order.getBuyPrice(), actualPrice) > 0.5) {
            return true;
        } else if (MathUtil.getPercentageProfit(order.getBuyPrice(), actualPrice) < -3) {
            return true;
        } else {
            return false;
        }
    }
}
