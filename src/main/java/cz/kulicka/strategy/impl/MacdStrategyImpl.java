package cz.kulicka.strategy.impl;

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

    static Logger log = Logger.getLogger(MacdStrategyImpl.class);

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
    public boolean buy(Ticker ticker, double actualBTCUSDT) {

        if (orderService.getAllActiveBySymbol(ticker.getSymbol()).size() > 0) {
            log.debug("Already bought ticker symbol: " + ticker.getSymbol());
            return false;
        }

        log.debug("Try to make order for ticker symbol: " + ticker.getSymbol());

        TradingData tradingData = getTradingDataHistorical(ticker.getSymbol());

        log.debug(tradingData.toString());

        // order ??
        if (tradingData.getPreLastMacdHistogram() <= 0 && tradingData.getLastMacdHistogram() > 0) {
            log.debug("Make order for symbol: " + ticker.getSymbol());

            double lastPriceBTC = Double.parseDouble(binanceApiService.getLastPrice(ticker.getSymbol()).getPrice());
            double lastPriceInUSDT = lastPriceBTC * actualBTCUSDT;

            Order newOrder = new Order(ticker.getSymbol(), DateTimeUtils.getCurrentServerDate().getTime(), propertyPlaceholder.getPricePerOrderUSD(), lastPriceInUSDT,
                    propertyPlaceholder.getTradeBuyFee(), propertyPlaceholder.getTradeSellFee(), lastPriceBTC);
            newOrder.setActive(true);
            newOrder.setRiskValue(2);

            Order orderWithId = orderService.create(newOrder);

            ArrayList<Float> macdList = new ArrayList<>();
            macdList.add(tradingData.getPreLastMacdHistogram());
            //add only finished histogram candle
            //macdList.add(tradingData.getLastMacdHistogram());

            MacdIndicator macdIndicator = new MacdIndicator();
            macdIndicator.setBuyTime(orderWithId.getBuyTime());
            macdIndicator.setMacdList(macdList);
            macdIndicator.setOrderId(orderWithId.getId());
            macdIndicator.setMacdBuy(tradingData.getLastMacdHistogram());
            macdIndicator.setSymbol(orderWithId.getSymbol());
            //add only finished histogram candle data
            macdIndicator.setEmaLongYesterday(tradingData.getPrelastEmaLongYesterday());
            macdIndicator.setEmaShortYesterday(tradingData.getPreLastEmaShortYesterday());
            macdIndicator.setEmaSignalYesterday(tradingData.getPreLastEmaSignalYesterday());

            log.debug(macdIndicator.toString());

            macdIndicatorService.create(macdIndicator);

            return true;
        }
        return false;
    }

    @Override
    public boolean sell(Order order, double actualSellPriceForOrderWithFee) {

        log.debug("Sell order: " + order.toString());

        double actualPercentageProfit = MathUtil.getPercentageProfit(order.getBuyPriceForOrderWithFee(), actualSellPriceForOrderWithFee);
        double lastPriceBTC = Double.parseDouble(binanceApiService.getLastPrice(order.getSymbol()).getPrice());
        double actualPercentageProfitBTC = MathUtil.getPercentageProfit(order.getBuyPriceBTCForUnit(), lastPriceBTC);

        List<Candlestick> candlesticks = binanceApiService.getCandlestickBars(order.getSymbol(), propertyPlaceholder.getBinanceCandlesticksPeriod(), 2);

        ArrayList<Float> lastPrices = new ArrayList<>();

        for (Candlestick candlestick : candlesticks) {
            lastPrices.add(Float.parseFloat(candlestick.getClose()));
        }

        MacdIndicator macdIndicator = macdIndicatorService.getMacdIndicatorByOrderId(order.getId());

        log.debug(macdIndicator.toString());

        TradingData tradingData = MathUtil.getTradingData(order.getSymbol(), order.getId(), lastPrices,
                propertyPlaceholder.getEmaShortConstant(), propertyPlaceholder.getEmaLongConstant(), propertyPlaceholder.getEmaSignalConstant(),
                macdIndicator.getEmaShortYesterday(), macdIndicator.getEmaLongYesterday(), macdIndicator.getEmaSignalYesterday());

        log.debug(tradingData.toString());

        macdIndicator.setEmaShortYesterday(tradingData.getPreLastEmaShortYesterday());
        macdIndicator.setEmaLongYesterday(tradingData.getPrelastEmaLongYesterday());
        macdIndicator.setEmaSignalYesterday(tradingData.getPreLastEmaSignalYesterday());
        macdIndicator.getMacdList().add(tradingData.getPreLastMacdHistogram());
        macdIndicatorService.update(macdIndicator);

        log.debug(macdIndicator.toString());

        log.info("Sell? Symbol: " + order.getSymbol() + ", percentageProfit: " + String.format("%.9f", actualPercentageProfit) + " %  == "
                + String.format("%.9f", actualSellPriceForOrderWithFee - order.getBuyPriceForOrderWithFee()) + " $ MACDHistoLast-open " + String.format("%.9f", tradingData.getLastMacdHistogram()));

        order.setPercentageProfitFeeIncluded(actualPercentageProfit);
        order.setPercentageProfitBTCForUnitWithoutFee(actualPercentageProfitBTC);

        if (actualPercentageProfitBTC > propertyPlaceholder.getTakeProfitPercentage()) {
            log.info("Border CRACKED! SELL AND GET MY MONEY!!!");
            order.setSellReason(0);
            return true;
        } else if (actualPercentageProfitBTC < propertyPlaceholder.getStopLossPercentage() || tradingData.getPreLastMacdHistogram() < 0) {

            if (tradingData.getLastMacdHistogram() > 0 && tradingData.getPreLastMacdHistogram() < 0) {
                log.info("HODL over last macd was red, but last open macd is green - protect rebuy");
                log.info("Percengate profit BTC: " + actualPercentageProfitBTC);
                log.info("Pre last macd - closed: " + tradingData.getPreLastMacdHistogram());
                return false;
            }

            if (actualPercentageProfitBTC < propertyPlaceholder.getStopLossPercentage() && !(tradingData.getPreLastMacdHistogram() < 0)) {
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
    public boolean instaSell(Order order, double actualSellPriceForOrderWithFee) {
        double lastPriceBTC = Double.parseDouble(binanceApiService.getLastPrice(order.getSymbol()).getPrice());
        double actualPercentageProfitBTC = MathUtil.getPercentageProfit(order.getBuyPriceBTCForUnit(), lastPriceBTC);
        double actualPercentageProfit = MathUtil.getPercentageProfit(order.getBuyPriceForOrderWithFee(), actualSellPriceForOrderWithFee);

        if (actualPercentageProfitBTC > propertyPlaceholder.getTakeProfitInstaSellPercentage()) {
            log.info("INSTA SELL!!! - TAKE PROFIT");
            order.setPercentageProfitFeeIncluded(actualPercentageProfit);
            order.setPercentageProfitBTCForUnitWithoutFee(actualPercentageProfitBTC);
            order.setSellReason(3);
            return true;
        } else if (actualPercentageProfitBTC < propertyPlaceholder.getStopLossPercentage()) {
            log.info("INSTA SELL!!! - STOPLOSS");
            order.setPercentageProfitFeeIncluded(actualPercentageProfit);
            order.setPercentageProfitBTCForUnitWithoutFee(actualPercentageProfitBTC);
            order.setSellReason(4);
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

        return MathUtil.getTradingData(symbol, null, lastPrices, propertyPlaceholder.getEmaShortConstant(), propertyPlaceholder.getEmaLongConstant(), propertyPlaceholder.getEmaSignalConstant(),
                0, 0, 0);
    }
}
