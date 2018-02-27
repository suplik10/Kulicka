package cz.kulicka.strategy.impl;

import com.google.common.collect.Iterables;
import cz.kulicka.PropertyPlaceholder;
import cz.kulicka.entity.*;
import cz.kulicka.enums.CandlestickInterval;
import cz.kulicka.services.BinanceApiService;
import cz.kulicka.services.MacdIndicatorService;
import cz.kulicka.services.OrderService;
import cz.kulicka.strategy.OrderStrategy;
import cz.kulicka.utils.MathUtil;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MacdStrategy implements OrderStrategy {

    static Logger log = Logger.getLogger(SecondDumbStrategyImpl.class);

    private BinanceApiService binanceApiService;

    private MacdIndicatorService macdIndicatorService;

    private PropertyPlaceholder propertyPlaceholder;

    private OrderService orderService;

    public MacdStrategy(BinanceApiService binanceApiService, MacdIndicatorService macdIndicatorService, OrderService orderService, PropertyPlaceholder propertyPlaceholder) {
        this.binanceApiService = binanceApiService;
        this.macdIndicatorService = macdIndicatorService;
        this.orderService = orderService;
        this.propertyPlaceholder = propertyPlaceholder;
    }

    @Override
    public boolean buy(Ticker ticker, List<Order> activeOrders, double actualBTCUSDT) {

        for (Order order : activeOrders) {
            if (ticker.getSymbol().equals(order.getSymbol())) {
                return false;
            }
        }

        EmaMacd emaMacd = getMacdLastIndicator(ticker.getSymbol());

        // order ??
        if(emaMacd.getPreLastMacd() <= 0 && emaMacd.getLastMacd() > 0  ){

            double lastPriceInUSDT = Double.parseDouble(binanceApiService.getLastPrice(ticker.getSymbol()).getPrice()) * actualBTCUSDT;
            Order newOrder = new Order(ticker.getSymbol(), new Date().getTime(), propertyPlaceholder.getPricePerOrderUSD(), lastPriceInUSDT,
                    propertyPlaceholder.getTradeBuyFee(), propertyPlaceholder.getTradeSellFee());
            newOrder.setActive(true);
            newOrder.setRiskValue(2);

            Order orderWithId = orderService.create(newOrder);

            ArrayList<Float> macdList = new ArrayList<>();
            macdList.add(emaMacd.getPreLastMacd());
            macdList.add(emaMacd.getLastMacd());

            MacdIndicator macdIndicator = new MacdIndicator();
            macdIndicator.setBuyTime(orderWithId.getBuyTime());
            macdIndicator.setMacdList(macdList);
            macdIndicator.setOrderId(orderWithId.getId());
            macdIndicator.setMacdBuy(emaMacd.getLastMacd());
            macdIndicator.setSymbol(orderWithId.getSymbol());
            macdIndicator.setEmaLongYesterday(emaMacd.getEmaLongYesterday());
            macdIndicator.setEmaShortYesterday(emaMacd.getEmaShortYesterday());

            macdIndicatorService.create(macdIndicator);

            return true;
        }

        return false;

    }

    @Override
    public boolean sell(Order order, double actualSellPriceForOrderWithFee) {
        double actualPercentageProfit = MathUtil.getPercentageProfit(order.getBuyPriceForOrderWithFee(), actualSellPriceForOrderWithFee);

        log.info("Sell? Symbol: " + order.getSymbol() + ", actualRealPercentageProfit from bought price: " + String.format("%.9f", actualPercentageProfit) + " %  == "
                + String.format("%.9f", actualSellPriceForOrderWithFee - order.getBuyPriceForOrderWithFee()) + " $ buy price " + order.getBuyPriceForOrderWithFee());

        List<Candlestick> candlesticks = binanceApiService.getCandlestickBars(order.getSymbol(), CandlestickInterval.FIVE_MINUTES, 2);

        candlesticks.remove(candlesticks.size() - 1);

        MacdIndicator macdIndicator = macdIndicatorService.getMacdIndicatorByOrderId(order.getId());

        float emaShort = CalculateEMA(Float.parseFloat(candlesticks.get(0).getClose()), 12, macdIndicator.getEmaShortYesterday());
        float emaLong = CalculateEMA(Float.parseFloat(candlesticks.get(0).getClose()), 26, macdIndicator.getEmaLongYesterday());
        float macdLast = emaShort - emaLong;

        macdIndicator.setEmaShortYesterday(emaShort);
        macdIndicator.setEmaLongYesterday(emaLong);
        macdIndicator.getMacdList().add(macdLast);

        if(macdLast < 0){
            log.info("Border CRACKED! SELL AND GET MY MONEY!!!");
            return true;
        }
        log.info("HODL!!!");
        return false;
    }


    private EmaMacd getMacdLastIndicator(String symbol) {

        ArrayList<Float> emaShort = new ArrayList<>();
        ArrayList<Float> emaLong = new ArrayList<>();

        List<Candlestick> candlesticks = binanceApiService.getCandlestickBars(symbol, CandlestickInterval.FIVE_MINUTES, 300);

        //remove last one because he is variable at now
        candlesticks.remove(candlesticks.size() - 1);

        emaCalc(emaShort, candlesticks, 12);

        emaCalc(emaLong, candlesticks, 26);

        EmaMacd emaMacd = new EmaMacd(emaLong.get(emaLong.size() - 1), emaShort.get(emaShort.size() - 1),emaShort.get(emaShort.size() - 1) - emaLong.get(emaLong.size() - 1),emaShort.get(emaShort.size() - 2) - emaLong.get(emaLong.size() - 2));

        return emaMacd;
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
