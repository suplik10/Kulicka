package cz.kulicka.strategy.impl;

import com.google.common.collect.Iterables;
import cz.kulicka.PropertyPlaceholder;
import cz.kulicka.entity.Candlestick;
import cz.kulicka.entity.MacdIndicator;
import cz.kulicka.entity.Order;
import cz.kulicka.entity.Ticker;
import cz.kulicka.entity.TradingData;
import cz.kulicka.enums.OrderBuyReason;
import cz.kulicka.enums.OrderSellReason;
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

        if (orderService.getAllOpenBySymbol(ticker.getSymbol()).size() > 0) {
            log.debug("Already open order for ticker symbol: " + ticker.getSymbol());
            return false;
        }

        log.debug("Try to make order for ticker symbol: " + ticker.getSymbol());

        TradingData tradingData = getFullTradingDataHistorical(ticker.getSymbol(), propertyPlaceholder.getBinanceCandlesticksPeriod(), propertyPlaceholder.getEmaCountCandlesticks(),
                propertyPlaceholder.getEmaShortConstant(), propertyPlaceholder.getEmaLongConstant(), propertyPlaceholder.getEmaSignalConstant());

        log.debug(tradingData.toString());

        // order ??
        if (tradingData.getPreLastMacdHistogram() <= 0 && tradingData.getLastMacdHistogram() > 0 && isUptrend(ticker)) {
            log.debug("Make order for symbol: " + ticker.getSymbol());

            double lastPriceBTC = Double.parseDouble(binanceApiService.getLastPrice(ticker.getSymbol()).getPrice());
            double lastPriceInUSDT = lastPriceBTC * actualBTCUSDT;

            Order newOrder = new Order(ticker.getSymbol(), DateTimeUtils.getCurrentServerDate().getTime(), propertyPlaceholder.getPricePerOrderUSD(), lastPriceInUSDT,
                    propertyPlaceholder.getTradeBuyFee(), propertyPlaceholder.getTradeSellFee(), lastPriceBTC, OrderBuyReason.MACD_BUY.getCST());
            newOrder.setActive(true);
            newOrder.setOpen(true);

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
    public boolean rebuyStopLossProtection(Ticker ticker, double actualBTCUSDT) {
        List<Order> openOrders = orderService.getAllOpenButNotActiveBySymbol(ticker.getSymbol());

        if (openOrders.size() == 0) {
            log.debug("RE-BUY - No open order for ticker: " + ticker.getSymbol());
            return false;
        }

        Order openOrder = Iterables.getFirst(openOrders, null);

        double lastPriceBTC = Double.parseDouble(binanceApiService.getLastPrice(openOrder.getSymbol()).getPrice());

        log.debug("RE-BUY - Try to make order, symbol: " + ticker.getSymbol());

        TradingData tradingData = getFullTradingDataHistorical(ticker.getSymbol(), propertyPlaceholder.getBinanceCandlesticksPeriod(),
                propertyPlaceholder.getEmaCountCandlesticks(), propertyPlaceholder.getEmaShortConstant(),
                propertyPlaceholder.getEmaLongConstant(), propertyPlaceholder.getEmaSignalConstant());

        log.debug("RE-BUY" + tradingData.toString());

        if (lastPriceBTC > openOrder.getSellPriceBTCForUnit() && isUptrend(ticker)) {
            log.debug("RE-BUY - Make order macd, symbol: " + tradingData.toString() + " parent id: " + openOrder.getId());

            double lastPriceInUSDT = lastPriceBTC * actualBTCUSDT;

            Order newOrder = new Order(ticker.getSymbol(), DateTimeUtils.getCurrentServerDate().getTime(), propertyPlaceholder.getPricePerOrderUSD(), lastPriceInUSDT,
                    propertyPlaceholder.getTradeBuyFee(), propertyPlaceholder.getTradeSellFee(), lastPriceBTC, OrderBuyReason.STOPLOSS_PROTECTION_REBUY.getCST());
            newOrder.setActive(true);
            newOrder.setOpen(true);
            newOrder.setStopLossPriceValue(openOrder.getSellPriceBTCForUnit());
            newOrder.setParentId(openOrder.getId());

            Order newOrderWithId = orderService.create(newOrder);

            MacdIndicator oldMacdIndicator = macdIndicatorService.getMacdIndicatorByOrderId(openOrder.getId());

            MacdIndicator macdIndicatorForNewOrder = MacdIndicator.createNewInstance(oldMacdIndicator, newOrderWithId.getId(), newOrderWithId.getBuyTime(), tradingData.getLastMacdHistogram());

            log.debug(newOrder.toString());
            log.debug(macdIndicatorForNewOrder.toString());

            macdIndicatorService.create(macdIndicatorForNewOrder);

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

        TradingData tradingData = checkMacdIndicatorAndGetTradingData(order);

        log.info("Sell? Symbol: " + order.getSymbol() + ", percentageProfit: " + String.format("%.9f", actualPercentageProfit) + " %  == "
                + String.format("%.9f", actualSellPriceForOrderWithFee - order.getBuyPriceForOrderWithFee()) + " $ MACDHistoLast-open " + String.format("%.9f", tradingData.getLastMacdHistogram()));

        order.setPercentageProfitFeeIncluded(actualPercentageProfit);
        order.setPercentageProfitBTCForUnitWithoutFee(actualPercentageProfitBTC);

        if (actualPercentageProfitBTC > propertyPlaceholder.getTakeProfitPercentage()) {
            log.info("Border CRACKED! SELL AND GET MY MONEY!!!");
            order.setSellReason(OrderSellReason.CANDLESTICK_PERIOD_TAKE_PROFIT.getCST());
            order.setOpen(false);
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
                order.setSellReason(OrderSellReason.CANDLESTICK_PERIOD_STOPLOSS.getCST());
            } else {
                log.info("PANIC SELL!!! - MACD");
                order.setSellReason(OrderSellReason.CANDLESTICK_PERIOD_NEGATIVE_MACD.getCST());
                order.setOpen(false);
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
            order.setSellReason(OrderSellReason.INSTA_SELL_TAKE_PROFIT.getCST());
            return true;
        } else if (actualPercentageProfitBTC < propertyPlaceholder.getStopLossPercentage() || sellByStopLostProtection(order, lastPriceBTC)) {
            log.info("INSTA SELL!!! - STOPLOSS");
            order.setPercentageProfitFeeIncluded(actualPercentageProfit);
            order.setPercentageProfitBTCForUnitWithoutFee(actualPercentageProfitBTC);
            order.setSellReason(OrderSellReason.INSTA_SELL_STOPLOSS.getCST());
            return true;
        } else {
            return false;
        }
    }

    private boolean sellByStopLostProtection(Order order, double lastPriceBTC) {
        if (!propertyPlaceholder.isStopLossProtection()) {
            return false;
        }

        if (lastPriceBTC > order.getStopLossPriceValue()) {
            return false;
        }

        return true;
    }

    private TradingData getFullTradingDataHistorical(String symbol, String candlestickPeriod, int candlecticksCount, int emaShort, int emaLong, int emaSignal) {
        return MathUtil.getTradingData(symbol, null, getCandlesticksValues(symbol, candlestickPeriod, candlecticksCount), emaShort, emaLong, emaSignal,
                0, 0, 0);
    }

    private TradingData getEmaTradingDataHistorical(String symbol, String candlestickPeriod, int candlecticksCount, int emaShort, int emaLong) {
        return MathUtil.getEmaShortLongTradingData(symbol, null, getCandlesticksValues(symbol, candlestickPeriod, candlecticksCount), emaShort, emaLong,
                0, 0);
    }

    private ArrayList<Float> getCandlesticksValues(String symbol, String candlestickPeriod, int candlecticksCount){
        List<Candlestick> candlesticks = binanceApiService.getCandlestickBars(symbol, candlestickPeriod, candlecticksCount);

        //remove last candle to get more stable signal!
        //TODO time check to get best signal, if x seconds to closed date dont erase last macd
        //candlesticks.remove(candlesticks.size() - 1);

        ArrayList<Float> lastPrices = new ArrayList<>();

        for (Candlestick candlestick : candlesticks) {
            lastPrices.add(Float.parseFloat(candlestick.getClose()));
        }

        return lastPrices;
    }

    @Override
    public boolean closeNonActiveOpenOrder(Order order) {
        TradingData tradingData = checkMacdIndicatorAndGetTradingData(order);
        return tradingData.getPreLastMacdHistogram() < 0 ? true : false;
    }

    private TradingData checkMacdIndicatorAndGetTradingData(Order order) {
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

        return tradingData;
    }

    private boolean isUptrend(Ticker ticker) {

        if (!propertyPlaceholder.isCheckUptrendEmaStrategy()) {
            return true;
        }

        log.debug("IS uptrend for ticker: " + ticker.getSymbol());

        TradingData tradingData = getEmaTradingDataHistorical(ticker.getSymbol(), propertyPlaceholder.getEmaUptrendEmaStrategyCandlestickPeriod(),
                propertyPlaceholder.getEmaUptrendEmaStrategyCandlestickCount(), propertyPlaceholder.getEmaUptrendEmaStrategyShortEma(),
                propertyPlaceholder.getEmaUptrendEmaStrategyLongEma());

        log.debug(tradingData);

        return tradingData.getLastEmaShortYesterday() > tradingData.getLastEmaLongYesterday() ? true : false;
    }
}
