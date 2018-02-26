package cz.kulicka.strategy.impl;

import cz.kulicka.entity.Candlestick;
import cz.kulicka.entity.Order;
import cz.kulicka.entity.Ticker;
import cz.kulicka.enums.CandlestickInterval;
import cz.kulicka.services.BinanceApiService;
import cz.kulicka.strategy.OrderStrategy;
import cz.kulicka.utils.MathUtil;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class MacdStrategy implements OrderStrategy {

    static Logger log = Logger.getLogger(SecondDumbStrategyImpl.class);

    private BinanceApiService binanceApiService;

    public MacdStrategy(BinanceApiService binanceApiService) {
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

        List<Float> lastTwoIndicators = getMacdLastIndicator(ticker.getSymbol());

        //start order
        if(lastTwoIndicators.get(0) <= 0 && lastTwoIndicators.get(1) > 0  ){
            return true;
        }

        return false;

    }

    @Override
    public boolean sell(Order order, double actualSellPriceForOrderWithFee) {
        double actualPercentageProfit = MathUtil.getPercentageProfit(order.getBuyPriceForOrderWithFee(), actualSellPriceForOrderWithFee);

        log.info("Sell? Symbol: " + order.getSymbol() + ", actualRealPercentageProfit from bought price: " + String.format("%.9f", actualPercentageProfit) + " %  == "
                + String.format("%.9f", actualSellPriceForOrderWithFee - order.getBuyPriceForOrderWithFee()) + " $ buy price " + order.getBuyPriceForOrderWithFee());


        List<Float> lastTwoIndicators = getMacdLastIndicator(order.getSymbol());

        if(lastTwoIndicators.get(1) < 0){
            log.info("Border CRACKED! SELL AND GET MY MONEY!!!");
            return true;
        }
        log.info("HODL!!!");
        return false;
    }


    private List<Float> getMacdLastIndicator(String symbol) {

        ArrayList<Float> emaShort = new ArrayList<>();
        ArrayList<Float> emaLong = new ArrayList<>();
        List<Float> lastTwoIndicators = new ArrayList<>();

        List<Candlestick> candlesticks = binanceApiService.getCandlestickBars(symbol, CandlestickInterval.FIVE_MINUTES, 300);

        //remove last one because he is variable at now
        candlesticks.remove(candlesticks.size() - 1);

        emaCalc(emaShort, candlesticks, 12);

        emaCalc(emaLong, candlesticks, 26);

        lastTwoIndicators.add(emaShort.get(emaShort.size() - 2) - emaLong.get(emaLong.size() - 2));
        lastTwoIndicators.add(emaShort.get(emaShort.size() - 1) - emaLong.get(emaLong.size() - 1));

        return lastTwoIndicators;
    }

    private float CalculateEMA(float closingPrice, float numberOfDays, float EMAYesterday) {
        float multiplier = 2 / (numberOfDays + 1);
        return (closingPrice - EMAYesterday) * multiplier + EMAYesterday;
    }

    private void emaCalc(ArrayList<Float> emaList, List<Candlestick> klines, float days) {

        float ema;
        float yesterdayEMA = 0;

        for (Candlestick kline : klines) {
            //call the EMA calculation
            ema = CalculateEMA(Float.parseFloat(kline.getClose()), days, yesterdayEMA);
            //put the calculated ema in an array
            emaList.add(ema);
            //make sure yesterdayEMA gets filled with the EMA we used this time around
            yesterdayEMA = ema;
        }
    }
}
