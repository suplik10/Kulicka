package cz.kulicka.strategy.impl;

import cz.kulicka.entity.Candlestick;
import cz.kulicka.entity.Order;
import cz.kulicka.entity.Ticker;
import cz.kulicka.service.BinanceApiService;
import cz.kulicka.strategy.OrderStrategy;
import cz.kulicka.util.MathUtil;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.List;

public class SecondDumbStrategyImpl implements OrderStrategy {

    static Logger log = Logger.getLogger(SecondDumbStrategyImpl.class);

    private BinanceApiService binanceApiService;

    public SecondDumbStrategyImpl(BinanceApiService binanceApiService) {
        this.binanceApiService = binanceApiService;
    }


    public boolean buy(Ticker ticker, List<Order> activeOrders) {
        boolean createOrder = false;

        for (Order order : activeOrders) {
            if (ticker.getSymbol().equals(order.getSymbol())) {
                return false;
            }
        }

        List<Candlestick> candlestickList = binanceApiService.getCandlestickBars(ticker.getSymbol(), "1m", 4);
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
    public boolean buy(Ticker ticker, double actualBTCUSDT) {
        return false;
    }

    @Override
    public boolean sell(Order order, double actualSellPriceForOrderWithFee) {

        double actualPercentageProfit = MathUtil.getPercentageProfit(order.getBuyPriceForOrderWithFee(), actualSellPriceForOrderWithFee);
        //double actualSteppedPercengateProfit = MathUtil.getPercentageProfit(order.getSteppedBuyPriceForOrderWithFee(), actualSellPriceForOrderWithFee);

        log.info("Sell? Symbol: " + order.getSymbol() + ", actualRealPercentageProfit from bought price: " + String.format("%.9f", actualPercentageProfit) + " %  == " + String.format("%.9f", actualSellPriceForOrderWithFee - order.getBuyPriceForOrderWithFee()) + " $ buy price " + order.getBuyPriceForOrderWithFee());
        //log.info("Sell? Symbol: " + order.getSymbol() + ", actualSteppedPercengateProfit from stepped price: " + String.format("%.9f", actualSteppedPercengateProfit) + " %  == " + String.format("%.9f", actualSellPriceForOrderWithFee - order.getSteppedBuyPriceForOrderWithFee()) + " $ stepped price " + order.getSteppedBuyPriceForOrderWithFee());


        if (actualPercentageProfit > 0.05) {
            log.info("Border CRACKED! SELL AND GET MY MONEY!!!");
            order.setSteppedBuyPriceForOrderWithFee(actualSellPriceForOrderWithFee);
            return true;
        } else if (actualPercentageProfit < -1.5) {
            //stop-loss
            log.info("PANIC SELL!!!");
            return true;
        } else {
            //HODL, HODL, HOOOOODDDDLLLLLLLLL!!!
            return false;
        }
    }

    @Override
    public boolean instaSell(Order order, double actualSellPriceForOrderWithFee) {
        return false;
    }


}
