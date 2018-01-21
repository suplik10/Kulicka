package cz.kulicka.services.impl;

import cz.kulicka.entities.Candlestick;
import cz.kulicka.entities.Order;
import cz.kulicka.entities.Ticker;
import cz.kulicka.enums.CandlestickInterval;
import cz.kulicka.services.BinanceApiService;
import cz.kulicka.services.OrderStrategyService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderStrategyServiceImpl implements OrderStrategyService {

    static Logger log = Logger.getLogger(OrderStrategyServiceImpl.class);

    @Autowired
    BinanceApiService binanceApiService;

    @Override
    public boolean firstTestingBuyStrategy(Ticker ticker) {
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
    public boolean firstTestingSellStrategy(Order order) {
        if (order.getRiskValue() > 1) {
            order.setRiskValue(order.getRiskValue() - 1);
            log.info("Order resuming id: " + order.getId());
            return false;
        } else {
            return true;
        }
    }
}
