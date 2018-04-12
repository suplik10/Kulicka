package cz.kulicka.strategy.impl;

import com.google.common.collect.Iterables;
import cz.kulicka.PropertyPlaceholder;
import cz.kulicka.entity.Order;
import cz.kulicka.entity.Ticker;
import cz.kulicka.entity.TradingData;
import cz.kulicka.enums.OrderBuyReason;
import cz.kulicka.enums.OrderSellReason;
import cz.kulicka.service.BinanceApiService;
import cz.kulicka.service.MacdIndicatorService;
import cz.kulicka.service.OrderService;
import cz.kulicka.strategy.OrderStrategy;
import cz.kulicka.util.MathUtil;
import org.apache.log4j.Logger;

import java.util.List;

public class MacdStrategyImpl extends AbstractStrategy implements OrderStrategy {

    static Logger log = Logger.getLogger(MacdStrategyImpl.class);

    public MacdStrategyImpl(BinanceApiService binanceApiService, MacdIndicatorService macdIndicatorService, OrderService orderService, PropertyPlaceholder propertyPlaceholder) {
        super(binanceApiService, macdIndicatorService, orderService, propertyPlaceholder);
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
            makeOrder(ticker, actualBTCUSDT, tradingData, OrderBuyReason.MACD_BUY);
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

        log.debug("RE-BUY " + tradingData.toString());

        if ((MathUtil.getPercentageDifference(openOrder.getSellPriceBTCForUnit(), lastPriceBTC) > propertyPlaceholder.getStopLossProtectionPercentageIntolerantion()) && isUptrend(ticker)) {
            makeOrderByStopLossProtection(openOrder, tradingData, lastPriceBTC, actualBTCUSDT);
            return true;
        }

        return false;
    }

    @Override
    public boolean sell(Order order, double actualBTCUSDT, double lastPriceBTC) {

        log.debug("Sell? order: " + order.toString());

        double actualPercentageProfitBTC = MathUtil.getPercentageDifference(order.getBuyPriceBTCForUnit(), lastPriceBTC);

        TradingData tradingData = checkMacdIndicatorAndGetTradingData(order);

        log.info("Sell? Symbol: " + order.getSymbol() + ", percentageProfitBTCWIthoutFee:  " + String.format("%.9f", actualPercentageProfitBTC) + " % "
                + " MACDHistoLast-open " + String.format("%.9f", tradingData.getLastMacdHistogram()));

        if (propertyPlaceholder.isTrailingStopStrategy() && order.isTrailingStop()) {
            return handleTrailingStopOrder(order, actualBTCUSDT, actualPercentageProfitBTC, lastPriceBTC);
        } else {
            if (actualPercentageProfitBTC > propertyPlaceholder.getTakeProfitPercentage()) {
                if (propertyPlaceholder.isTrailingStopStrategy()) {
                    log.debug("Border CRACKED! but trailing stop enabled for symbol: " + order.getSymbol());
                    order.setTrailingStop(true);
                    return false;
                }
                log.info("Border CRACKED! SELL AND GET MY MONEY!!!");
                setOrderForSell(order, actualBTCUSDT, actualPercentageProfitBTC, OrderSellReason.CANDLESTICK_PERIOD_TAKE_PROFIT, lastPriceBTC, true);
                return true;
            } else if (actualPercentageProfitBTC < propertyPlaceholder.getStopLossPercentage() || tradingData.getPreLastMacdHistogram() < 0) {

                if (tradingData.getLastMacdHistogram() > 0 && tradingData.getPreLastMacdHistogram() < 0) {
                    log.info("HODL over last macd was red, but last open macd is green - protect rebuy");
                    log.info("Percengate profit BTC: " + actualPercentageProfitBTC);
                    log.info("Pre last macd - closed: " + tradingData.getPreLastMacdHistogram());
                    return false;
                }

                if (tradingData.getPreLastMacdHistogram() < 0) {
                    log.info("PANIC SELL!!! - MACD");
                    setOrderForSell(order, actualBTCUSDT, actualPercentageProfitBTC, OrderSellReason.CANDLESTICK_PERIOD_NEGATIVE_MACD, lastPriceBTC, true);
                } else {
                    log.info("PANIC SELL!!! - STOPLOSS");
                    setOrderForSell(order, actualBTCUSDT, actualPercentageProfitBTC, OrderSellReason.CANDLESTICK_PERIOD_STOPLOSS, lastPriceBTC, !propertyPlaceholder.isStopLossProtection());
                }

                return true;
            } else {
                //HODL, HODL, HOOOOODDDDLLLLLLLLL!!!
                return false;
            }
        }
    }

    @Override
    public boolean instaSell(Order order, double actualBTCUSDT, double lastPriceBTC) {
        return handleInstaSell(order, actualBTCUSDT, lastPriceBTC);
    }

    @Override
    public boolean closeNonActiveOpenOrder(Order order) {
        TradingData tradingData = checkMacdIndicatorAndGetTradingData(order);
        return tradingData.getPreLastMacdHistogram() < 0 ? true : false;
    }
}
