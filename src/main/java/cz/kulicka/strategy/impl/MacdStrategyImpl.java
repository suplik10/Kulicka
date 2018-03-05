package cz.kulicka.strategy.impl;

import com.google.common.collect.Lists;
import cz.kulicka.PropertyPlaceholder;
import cz.kulicka.entity.*;
import cz.kulicka.service.BinanceApiService;
import cz.kulicka.service.MacdIndicatorService;
import cz.kulicka.service.OrderService;
import cz.kulicka.strategy.OrderStrategy;
import cz.kulicka.util.DateTimeUtils;
import cz.kulicka.util.MathUtil;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class MacdStrategyImpl implements OrderStrategy {

    static Logger log = Logger.getLogger(SecondDumbStrategyImpl.class);

    private BinanceApiService binanceApiService;

    private MacdIndicatorService macdIndicatorService;

    private PropertyPlaceholder propertyPlaceholder;

    private OrderService orderService;

    public MacdStrategyImpl(BinanceApiService binanceApiService, MacdIndicatorService macdIndicatorService, OrderService orderService, PropertyPlaceholder propertyPlaceholder) {
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

        TradingData tradingData = getTradingDataHistorical(ticker.getSymbol());

        // order ??
        if (tradingData.getPreLastMacdHistogram() <= 0 && tradingData.getLastMacdHistogram() > 0) {

            double lastPriceInUSDT = Double.parseDouble(binanceApiService.getLastPrice(ticker.getSymbol()).getPrice()) * actualBTCUSDT;
            Order newOrder = new Order(ticker.getSymbol(), DateTimeUtils.getCurrentServerDate().getTime(), propertyPlaceholder.getPricePerOrderUSD(), lastPriceInUSDT,
                    propertyPlaceholder.getTradeBuyFee(), propertyPlaceholder.getTradeSellFee());
            newOrder.setActive(true);
            newOrder.setRiskValue(2);

            Order orderWithId = orderService.create(newOrder);

            ArrayList<Float> macdList = new ArrayList<>();
            macdList.add(tradingData.getPreLastMacdHistogram());
            macdList.add(tradingData.getLastMacdHistogram());

            MacdIndicator macdIndicator = new MacdIndicator();
            macdIndicator.setBuyTime(orderWithId.getBuyTime());
            macdIndicator.setMacdList(macdList);
            macdIndicator.setOrderId(orderWithId.getId());
            macdIndicator.setMacdBuy(tradingData.getLastMacdHistogram());
            macdIndicator.setSymbol(orderWithId.getSymbol());
            macdIndicator.setEmaLongYesterday(tradingData.getEmaLongYesterday());
            macdIndicator.setEmaShortYesterday(tradingData.getEmaShortYesterday());
            macdIndicator.setEmaSignalYesterday(tradingData.getEmaSignalYesterday());

            macdIndicatorService.create(macdIndicator);

            return true;
        }
        return false;
    }

    @Override
    public boolean sell(Order order, double actualSellPriceForOrderWithFee) {
        double actualPercentageProfit = MathUtil.getPercentageProfit(order.getBuyPriceForOrderWithFee(), actualSellPriceForOrderWithFee);

        List<Candlestick> candlesticks = binanceApiService.getCandlestickBars(order.getSymbol(), propertyPlaceholder.getBinanceCandlesticksPeriod(), 1);

        MacdIndicator macdIndicator = macdIndicatorService.getMacdIndicatorByOrderId(order.getId());

        TradingData tradingData = MathUtil.getTradingData(Lists.newArrayList(Float.parseFloat(candlesticks.get(0).getClose())),
                propertyPlaceholder.getEmaShortConstant(), propertyPlaceholder.getEmaLongConstant(), propertyPlaceholder.getEmaSignalConstant(),
                macdIndicator.getEmaShortYesterday(), macdIndicator.getEmaLongYesterday(), macdIndicator.getEmaSignalYesterday());

        macdIndicator.setEmaShortYesterday(tradingData.getEmaShortYesterday());
        macdIndicator.setEmaLongYesterday(tradingData.getEmaLongYesterday());
        macdIndicator.setEmaSignalYesterday(tradingData.getEmaSignalYesterday());
        macdIndicator.getMacdList().add(tradingData.getLastMacdHistogram());
        macdIndicatorService.update(macdIndicator);

        log.info("Sell? Symbol: " + order.getSymbol() + ", percentageProfit: " + String.format("%.9f", actualPercentageProfit) + " %  == "
                + String.format("%.9f", actualSellPriceForOrderWithFee - order.getBuyPriceForOrderWithFee()) + " $ MACDHistoLast " + String.format("%.9f", tradingData.getLastMacdHistogram()));

        order.setPercentageProfitFeeIncluded(actualPercentageProfit);

        if (actualPercentageProfit > propertyPlaceholder.getTakeProfitPercentage()) {
            log.info("Border CRACKED! SELL AND GET MY MONEY!!!");
            order.setSellReason(0);
            return true;
        } else if (actualPercentageProfit < propertyPlaceholder.getStopLossPercentage() || tradingData.getLastMacdHistogram() < 0) {

            if (actualPercentageProfit < propertyPlaceholder.getStopLossPercentage() && !(tradingData.getLastMacdHistogram() < 0)) {
                log.info("PANIC SELL!!! - STOPLOSS");
                order.setSellReason(1);
            } else {
                log.info("PANIC SELL!!! - MACD");
                order.setSellReason(2);
            }
            return true;
        } else {
            //HODL, HODL, HOOOOODDDDLLLLLLLLL!!!
            return false;
        }
    }

    @Override
    public boolean instaSellForProfit(Order order, double actualSellPriceForOrderWithFee) {
        double actualPercentageProfit = MathUtil.getPercentageProfit(order.getBuyPriceForOrderWithFee(), actualSellPriceForOrderWithFee);

        if (actualPercentageProfit > propertyPlaceholder.getTakeProfitInstaSellPercentage()) {
            log.info("INSTA SELL TAKE PROFIT!!!");
            order.setPercentageProfitFeeIncluded(actualPercentageProfit);
            order.setSellReason(3);
            return true;
        } else {
            return false;
        }
    }

    private TradingData getTradingDataHistorical(String symbol) {

        List<Candlestick> candlesticks = binanceApiService.getCandlestickBars(symbol, propertyPlaceholder.getBinanceCandlesticksPeriod(), propertyPlaceholder.getEmaCountCandlesticks());

        //remove last candle to get more stable signal!
        //TODO time check to get best signal, if x seconds to closed date dont erase last macd
        //candlesticks.remove(candlesticks.size() - 1);

        ArrayList<Float> lastPrices = new ArrayList<>();

        for (Candlestick candlestick : candlesticks) {
            lastPrices.add(Float.parseFloat(candlestick.getClose()));
        }

        return MathUtil.getTradingData(lastPrices, propertyPlaceholder.getEmaShortConstant(), propertyPlaceholder.getEmaLongConstant(), propertyPlaceholder.getEmaSignalConstant(),
                0, 0, 0);
    }
}
