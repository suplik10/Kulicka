package cz.kulicka;

import cz.kulicka.constant.CurrenciesConstants;
import cz.kulicka.entity.ExchangeInfo;
import cz.kulicka.entity.Order;
import cz.kulicka.entity.Ticker;
import cz.kulicka.enums.StrategyEnum;
import cz.kulicka.service.BinanceApiService;
import cz.kulicka.service.MacdIndicatorService;
import cz.kulicka.service.OrderService;
import cz.kulicka.strategy.OrderStrategyContext;
import cz.kulicka.strategy.impl.EMAStrategyImpl;
import cz.kulicka.strategy.impl.MacdStrategyImpl;
import cz.kulicka.util.DateTimeUtils;
import cz.kulicka.util.IOUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class CoreEngine {

    static Logger log = Logger.getLogger(CoreEngine.class);

    public static Long DATE_DIFFERENCE_BETWEN_SERVER_AND_CLIENT_MILISECONDS;
    public static ExchangeInfo EXCHANGE_INFO_CONTEXT;

    @Autowired
    OrderService orderService;
    @Autowired
    BinanceApiService binanceApiService;
    @Autowired
    PropertyPlaceholder propertyPlaceholder;
    @Autowired
    OrderStrategyContext orderStrategyContext;
    @Autowired
    MacdIndicatorService macdIndicatorService;

    boolean mutex = false;

    public void synchronizeServerTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        log.info("------ Date synch ------");
        log.info("Date before synch: " + dateFormat.format(new Date()));
        Date dateFromServer = new Date(EXCHANGE_INFO_CONTEXT.getServerTime());
        log.info("Server date: " + dateFormat.format(dateFromServer));
        DATE_DIFFERENCE_BETWEN_SERVER_AND_CLIENT_MILISECONDS = new Date().getTime() - (dateFromServer.getTime());
        log.info("Date after synch: " + dateFormat.format(DateTimeUtils.getCurrentServerDate()));
    }

    public void loadExchangeContext() {
        EXCHANGE_INFO_CONTEXT = binanceApiService.getExchangeInfo();
    }

    public void setOrderStrategy(String strategy) {

        StrategyEnum strategyEnum = StrategyEnum.valueOf(strategy);

        switch (strategyEnum) {
            case MACD:
                log.info("----- MACD STRATEGY SET -----");
                orderStrategyContext.setOrderStrategy(new MacdStrategyImpl(binanceApiService, macdIndicatorService, orderService, propertyPlaceholder));
                break;
            case EMA:
                log.info("----- EMA STRATEGY SET -----");
                orderStrategyContext.setOrderStrategy(new EMAStrategyImpl(binanceApiService, macdIndicatorService, orderService, propertyPlaceholder));
                break;
            default:
                log.info("----- DEFAULT MACD STRATEGY SET -----");
                orderStrategyContext.setOrderStrategy(new MacdStrategyImpl(binanceApiService, macdIndicatorService, orderService, propertyPlaceholder));
        }
    }

    public void scanCurrenciesAndMakeNewOrders() {
        ArrayList<Ticker> newCurrencies = new ArrayList<>();
        ArrayList<Ticker> currencies;
        log.info("SCAN START!");

        currencies = binanceApiService.checkActualCurrencies(newCurrencies);
        log.debug("Number of currencies: " + currencies.size());

        double actualBTCUSDT = Double.parseDouble(binanceApiService.getLastPrice(CurrenciesConstants.BTCUSDT).getPrice());

        for (Ticker currencyTicker : currencies) {
            //Buy???
            if (orderStrategyContext.buy(currencyTicker, actualBTCUSDT)) {
                //TODO handle that!
                log.debug("buy now!");
            }

            if (propertyPlaceholder.isStopLossProtection()) {
                orderStrategyContext.rebuyStopLossProtection(currencyTicker, actualBTCUSDT);
            }
        }


        log.info("SCAN COMPLETE!");
    }

    public void handleActiveOrders(boolean instaSell) {
        List<Order> activeOrders = orderService.getAllActive();
        double actualBTCUSDT = Double.parseDouble(binanceApiService.getLastPrice(CurrenciesConstants.BTCUSDT).getPrice());
        boolean endOrder;
        boolean checkProfits = false;

        log.info("Handle ACTIVE orders start > " + activeOrders.size() + " active orders");

        for (Order order : activeOrders) {
            //Sell by strategy?

            double lastPriceBTC = Double.parseDouble(binanceApiService.getLastPrice(order.getSymbol()).getPrice());
            order.setActualPriceBTCForUnit(lastPriceBTC);

            if (instaSell) {
                endOrder = orderStrategyContext.instaSellForProfit(order, actualBTCUSDT, lastPriceBTC);
            } else {
                endOrder = orderStrategyContext.sell(order, actualBTCUSDT, lastPriceBTC);
            }

            if (endOrder) {
                log.info("Order STOPPED : " + order.toString());
                checkProfits = true;
            } else {
                if (!instaSell) {
                    log.info("Continuing order: " + order.toString());
                }
            }
            //orderService.saveAll(activeOrders);
        }
        log.info("Handle ACTIVE orders finished!");

        if (checkProfits) {
            checkProfits();
        }
        reportActiveOrders();
    }

    public void handleOpenOrders() {
        List<Order> openOrders = orderService.getAllOpenButNotActive();

        log.info("Handle OPEN orders start > " + openOrders.size() + " active orders");

        for (Order order : openOrders) {
            if (orderStrategyContext.closeNonActiveOpenOrder(order)) {
                log.debug("Close order id: " + order.getId() + " symbol: " + order.getSymbol());
                order.setOpen(false);
            }
        }

        log.info("Handle OPEN orders finished!");

        orderService.saveAll(openOrders);
    }

    public void checkProfits() {
        List<Order> finishedOrders = orderService.findAllByActiveFalseAndSellPriceForOrderWithFeeIsNotNull();

        double profit = 0;

        for (Order order : finishedOrders) {
            profit += order.getProfitFeeIncluded();
        }

        log.info("=================================== FINAL PROFIT: " + String.format("%.9f", (profit)) + " $$$ ===================================");

        IOUtil.saveOrderToCsv(new ArrayList<>(finishedOrders), propertyPlaceholder.getCsvReportFilePath(), false, propertyPlaceholder.getWhiteListCoins());
    }

    public void dailyReport() {
        List<Order> finishedOrders = orderService.findAllByActiveFalse();
        List<Order> dailyOrders = new ArrayList<>();

        for (Order order : finishedOrders) {
            if (DateTimeUtils.yesterday().getTime() < order.getSellTime()) {
                dailyOrders.add(order);
            }
        }

        log.info("Report finished order per day count > " + dailyOrders.size());

        IOUtil.ordersToCSV(new ArrayList<>(finishedOrders),
                DateTimeUtils.getPathWithDate(propertyPlaceholder.getCsvReportDailyFilePath(), DateTimeUtils.yesterday()), false);
    }

    public void reportActiveOrders() {
        List<Order> openOrders = orderService.getAllActive();

        log.info("Report active orders count > " + openOrders.size());

        IOUtil.activeOrdersToCSV(new ArrayList<>(openOrders), propertyPlaceholder.getCsvReportOpenOrdersFilePath(), false);
    }


    public void panicSell() {
        if (propertyPlaceholder.isCoinMachineOn()) {
            orderStrategyContext.panicSellAll();
        }
    }

    public boolean isMutex() {
        return mutex;
    }

    public void setMutex(boolean mutex) {
        this.mutex = mutex;
    }


}
