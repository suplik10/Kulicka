package cz.kulicka.strategy.impl;

import cz.kulicka.CoreEngine;
import cz.kulicka.PropertyPlaceholder;
import cz.kulicka.constant.BinanceApiConstants;
import cz.kulicka.entity.*;
import cz.kulicka.enums.OrderBuyReason;
import cz.kulicka.enums.OrderSellReason;
import cz.kulicka.exception.BinanceApiException;
import cz.kulicka.exception.OrderApiException;
import cz.kulicka.service.BinanceApiService;
import cz.kulicka.service.MacdIndicatorService;
import cz.kulicka.service.OrderService;
import cz.kulicka.util.DateTimeUtils;
import cz.kulicka.util.MathUtil;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class AbstractStrategy {

    static Logger log = Logger.getLogger(AbstractStrategy.class);

    protected BinanceApiService binanceApiService;
    protected MacdIndicatorService macdIndicatorService;
    protected PropertyPlaceholder propertyPlaceholder;
    protected OrderService orderService;

    protected AbstractStrategy(BinanceApiService binanceApiService, MacdIndicatorService macdIndicatorService, OrderService orderService, PropertyPlaceholder propertyPlaceholder) {
        this.binanceApiService = binanceApiService;
        this.macdIndicatorService = macdIndicatorService;
        this.orderService = orderService;
        this.propertyPlaceholder = propertyPlaceholder;
    }

    protected Order setOrderForSell(Order order, double actualBTCUSDT, double actualPercentageProfitBTC, OrderSellReason orderSellReason, double lastPriceBTC, boolean closeOrder) {
        double actualSellPriceForOrderWithFee = MathUtil.getSellPriceForOrderWithFee(order.getBoughtAmount(),
                lastPriceBTC * actualBTCUSDT, order.getSellFeeConstant());
        double actualPercentageProfit = MathUtil.getPercentageDifference(order.getBuyPriceForOrderWithFee(), actualSellPriceForOrderWithFee);

        order.setPercentageProfitFeeIncluded(actualPercentageProfit);
        order.setPercentageProfitBTCForUnitWithoutFee(actualPercentageProfitBTC);
        order.setSellPriceForOrderWithFee(actualSellPriceForOrderWithFee);
        order.setProfitFeeIncluded(order.getSellPriceForOrderWithFee() - order.getBuyPriceForOrderWithFee());
        order.setSellTime(DateTimeUtils.getCurrentServerDate().getTime());
        order.setSellReason(orderSellReason.getCST());
        order.setSellPriceBTCForUnit(lastPriceBTC);
        order.setOpen(!closeOrder);
        order.setActive(false);

        return order;
    }

    protected boolean sellByStopLostProtection(Order order, double lastPriceBTC) {
        if (!propertyPlaceholder.isStopLossProtection()) {
            return false;
        }

        if (lastPriceBTC > order.getStopLossPriceValue()) {
            return false;
        }

        return true;
    }

    protected TradingData getFullTradingDataHistorical(String symbol, String candlestickPeriod, int candlecticksCount, int emaShort, int emaLong, int emaSignal) {
        return MathUtil.getTradingData(symbol, null, getCandlesticksValues(symbol, candlestickPeriod, candlecticksCount), emaShort, emaLong, emaSignal,
                0, 0, 0);
    }

    protected TradingData getEmaTradingDataHistorical(String symbol, String candlestickPeriod, int candlecticksCount, int emaShort, int emaLong) {
        return MathUtil.getEmaShortLongTradingData(symbol, null, getCandlesticksValues(symbol, candlestickPeriod, candlecticksCount), emaShort, emaLong,
                0, 0);
    }

    protected TradingData getEmaTradingData(Order order) {

        MacdIndicator macdIndicator = macdIndicatorService.getMacdIndicatorByOrderId(order.getId());

        TradingData tradingData = MathUtil.getEmaShortLongTradingData(order.getSymbol(), null, getCandlesticksValues(order.getSymbol(), propertyPlaceholder.getBinanceCandlesticksPeriod(),
                2), propertyPlaceholder.getEmaStrategyShortEma(), propertyPlaceholder.getEmaStrategyLongEma(),
                macdIndicator.getEmaShortYesterday(), macdIndicator.getEmaLongYesterday());

        log.debug(tradingData.toString());

        macdIndicator.setEmaShortYesterday(tradingData.getPreLastEmaShortYesterday());
        macdIndicator.setEmaLongYesterday(tradingData.getPrelastEmaLongYesterday());
        macdIndicatorService.update(macdIndicator);

        log.debug(macdIndicator.toString());

        return tradingData;
    }

    protected TradingData checkMacdIndicatorAndGetTradingData(Order order) {

        MacdIndicator macdIndicator = macdIndicatorService.getMacdIndicatorByOrderId(order.getId());

        log.debug(macdIndicator.toString());

        TradingData tradingData = MathUtil.getTradingData(order.getSymbol(), order.getId(), getCandlesticksValues(order.getSymbol(), propertyPlaceholder.getBinanceCandlesticksPeriod(), 2),
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

    protected boolean isUptrend(Ticker ticker) {

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

    protected boolean handleTrailingStopOrder(Order order, double actualBTCUSDT, double actualPercentageProfitBTC, double lastPriceBTC) {

        log.debug("TRAILING STOP for symbol: " + order.getSymbol() + " actualPercentageProfitBTC: " + String.format("%.3f", actualPercentageProfitBTC)
                + " % actual TAKEPROFIT " + String.format("%.3f", order.getTrailingStopTakeProfitPercentage())
                + " % actual STOPLOSS " + String.format("%.3f", order.getTrailingStopStopLossPercentage()) + " %");

        if (actualPercentageProfitBTC > order.getTrailingStopTakeProfitPercentage()) {
            order.setTrailingStopTakeProfitPercentage(actualPercentageProfitBTC + propertyPlaceholder.getTrailingStopTakeProfitPlusPercentageConstant());
            order.setTrailingStopStopLossPercentage(actualPercentageProfitBTC + propertyPlaceholder.getTrailingStopStopLossMinusPercentageConstant());
            return false;
        } else if (actualPercentageProfitBTC < order.getTrailingStopStopLossPercentage()) {
            setOrderForSell(order, actualBTCUSDT, actualPercentageProfitBTC, OrderSellReason.INSTA_SELL_TRAILING_STOP_STOPLOSS, lastPriceBTC, !propertyPlaceholder.isStopLossProtection());
            return true;
        }
        return false;
    }

    protected void makeOrder(Ticker ticker, double actualBTCUSDT, TradingData tradingData, OrderBuyReason buyReason) {
        log.debug("Make order for symbol: " + ticker.getSymbol());

        double lastPriceBTC = Double.parseDouble(binanceApiService.getLastPrice(ticker.getSymbol()).getPrice());
        double lastPriceInUSDT = lastPriceBTC * actualBTCUSDT;

        //TODO important !!! aby naše nabídka byla první na řadě
        if (propertyPlaceholder.isCoinMachineOn()) {
            try {
                makeServerBuyOrder(ticker.getSymbol(), lastPriceBTC);
            } catch (OrderApiException e) {
                return;
            }
        }

        Order newOrder = new Order(ticker.getSymbol(), DateTimeUtils.getCurrentServerDate().getTime(), propertyPlaceholder.getPricePerOrderUSD(), lastPriceInUSDT,
                propertyPlaceholder.getTradeBuyFee(), propertyPlaceholder.getTradeSellFee(), lastPriceBTC, buyReason.getCST());
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
    }

    protected void makeOrderByStopLossProtection(Order openOrder, TradingData tradingData, double lastPriceBTC, double actualBTCUSDT) {
        log.debug("RE-BUY - Make order macd, symbol: " + tradingData.toString() + " parent id: " + openOrder.getId());

        double lastPriceInUSDT = lastPriceBTC * actualBTCUSDT;

        if (propertyPlaceholder.isCoinMachineOn()) {
            try {
                makeServerBuyOrder(openOrder.getSymbol(), lastPriceBTC);
            } catch (OrderApiException e) {
                return;
            }
        }

        Order newOrder = new Order(openOrder.getSymbol(), DateTimeUtils.getCurrentServerDate().getTime(), propertyPlaceholder.getPricePerOrderUSD(), lastPriceInUSDT,
                propertyPlaceholder.getTradeBuyFee(), propertyPlaceholder.getTradeSellFee(), lastPriceBTC, OrderBuyReason.STOPLOSS_PROTECTION_REBUY.getCST());
        newOrder.setActive(true);
        newOrder.setOpen(true);
        newOrder.setStopLossPriceValue(openOrder.getSellPriceBTCForUnit());
        newOrder.setParentId(openOrder.getId());

        Order newOrderWithId = orderService.create(newOrder);

        MacdIndicator oldMacdIndicator = macdIndicatorService.getMacdIndicatorByOrderId(openOrder.getId());

        MacdIndicator macdIndicatorForNewOrder = MacdIndicator.createNewInstance(oldMacdIndicator, newOrderWithId.getId(), newOrderWithId.getBuyTime(), tradingData);

        log.debug(newOrder.toString());
        log.debug(macdIndicatorForNewOrder.toString());

        log.debug("RE-BUY - close parent order id: " + openOrder.getId() + " symbol: " + openOrder.getSymbol());
        openOrder.setOpen(false);
        orderService.update(openOrder);

        macdIndicatorService.create(macdIndicatorForNewOrder);
    }

    private ArrayList<Float> getCandlesticksValues(String symbol, String candlestickPeriod, int candlecticksCount) {
        List<Candlestick> candlesticks = binanceApiService.getCandlestickBars(symbol, candlestickPeriod, candlecticksCount);

        if (propertyPlaceholder.isRemoveLastOpenCandlestick()) {
            candlesticks.remove(candlesticks.size() - 1);
        }

        ArrayList<Float> lastPrices = new ArrayList<>();

        for (Candlestick candlestick : candlesticks) {
            lastPrices.add(Float.parseFloat(candlestick.getClose()));
        }

        return lastPrices;
    }

    protected boolean handleInstaSell(Order order, double actualBTCUSDT, double lastPriceBTC) {
        double actualPercentageProfitBTC = MathUtil.getPercentageDifference(order.getBuyPriceBTCForUnit(), lastPriceBTC);

        if (propertyPlaceholder.isTrailingStopStrategy() && order.isTrailingStop()) {
            return handleTrailingStopOrder(order, actualBTCUSDT, actualPercentageProfitBTC, lastPriceBTC);
        } else {
            if (actualPercentageProfitBTC > propertyPlaceholder.getTakeProfitInstaSellPercentage()) {
                log.info("INSTA SELL!!! - TAKE PROFIT");
                if (propertyPlaceholder.isTrailingStopStrategy()) {
                    log.debug("INSTA SELL set TRAILING STOP for symbol: " + order.getSymbol());
                    order.setTrailingStop(true);
                    order.setTrailingStopTakeProfitPercentage(actualPercentageProfitBTC + propertyPlaceholder.getTrailingStopTakeProfitPlusPercentageConstant());
                    order.setTrailingStopStopLossPercentage(actualPercentageProfitBTC + propertyPlaceholder.getTrailingStopStopLossMinusPercentageConstant());
                    return false;
                }
                setOrderForSell(order, actualBTCUSDT, actualPercentageProfitBTC, OrderSellReason.INSTA_SELL_TAKE_PROFIT, lastPriceBTC, true);
                return true;
            } else if (actualPercentageProfitBTC < propertyPlaceholder.getStopLossPercentage() || sellByStopLostProtection(order, lastPriceBTC)) {
                log.info("INSTA SELL!!! - STOPLOSS");
                setOrderForSell(order, actualBTCUSDT, actualPercentageProfitBTC, OrderSellReason.INSTA_SELL_STOPLOSS, lastPriceBTC, !propertyPlaceholder.isStopLossProtection());
                return true;
            } else {
                return false;
            }
        }
    }

    protected void makeServerBuyOrder(String symbol, double lastPriceBTC) throws BinanceApiException {

        NewOrder newOrderBuy = null;
        NewOrderResponse newOrderBuyResponse = null;

        try {

            String quantity = Double.toString(MathUtil.cutDecimalsWithoutRound(propertyPlaceholder.getPricePerOrderBTC() / lastPriceBTC, CoreEngine.EXCHANGE_INFO_CONTEXT.getNumberOfDecimalPlacesToOrder(symbol)));

            newOrderBuy = NewOrder.marketBuy(symbol, quantity, DateTimeUtils.getCurrentServerTimeStamp());
            newOrderBuyResponse = binanceApiService.newOrder(newOrderBuy);

            log.info("==================================== SUCESSFULL BOUGHT COIN PROD ENVIROMENT ==========================================");
            log.info("===========" + newOrderBuyResponse + "=========");
        } catch (BinanceApiException e) {
            log.error("===================================== FATAL ERROR WHEN TRY TO BUY COIN ==========================================");
            log.error("===================================== OrderBuy > " + newOrderBuy);
            log.error("===================================== OrderBuyResponse > " + newOrderBuyResponse);
            log.error("===================================== BinanceApiException > " + e.getMessage());
            log.error("===================================== FATAL ERROR WHEN TRY TO BUY COIN ==========================================");
            throw new OrderApiException("Order BUY symbol " + symbol + " FAILED " + e.getMessage());
        }
    }

    protected void makeServerSellOrder(String symbol) throws BinanceApiException {

        NewOrder orderSell = null;
        NewOrderResponse orderSellResponse = null;

        try {
            Account acount = binanceApiService.getAccount(BinanceApiConstants.DEFAULT_RECEIVING_WINDOW, DateTimeUtils.getCurrentServerTimeStamp());
            AssetBalance assetBalance = acount.getAssetBalance(symbol.substring(0, symbol.length() - 3));

            String quantity = Double.toString(
                    MathUtil.cutDecimalsWithoutRound(Double.parseDouble(assetBalance.getFree()), CoreEngine.EXCHANGE_INFO_CONTEXT.getNumberOfDecimalPlacesToOrder(symbol)));

            orderSell = NewOrder.marketSell(symbol, quantity, DateTimeUtils.getCurrentServerTimeStamp());
            orderSellResponse = binanceApiService.newOrder(orderSell);

            log.info("==================================== SUCESSFULL SOLD COIN PROD ENVIROMENT ==========================================");
            log.info("===========" + orderSellResponse + "=========");

        } catch (BinanceApiException e) {
            log.error("==================================== FATAL ERROR WHEN TRY TO SELL COIN ==========================================");
            log.error("===================================== OrderSell > " + orderSell);
            log.error("===================================== OrderSellResponse > " + orderSellResponse);
            log.error("===================================== BinanceApiException > " + e.getMessage());
            log.error("==================================== FATAL ERROR WHEN TRY TO SELL COIN ==========================================");
            throw new OrderApiException("Order SELL symbol " + symbol + " FAILED " + e.getMessage());
        }
    }
}
