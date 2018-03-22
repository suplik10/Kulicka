package cz.kulicka;

import cz.kulicka.constant.CurrenciesConstants;
import cz.kulicka.entity.Order;
import cz.kulicka.entity.Ticker;
import cz.kulicka.repository.OrderRepository;
import cz.kulicka.service.BinanceApiService;
import cz.kulicka.service.MacdIndicatorService;
import cz.kulicka.service.OrderService;
import cz.kulicka.strategy.OrderStrategy;
import cz.kulicka.strategy.OrderStrategyContext;
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

    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;
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
        Date dateFromServer = new Date(binanceApiService.getServerTime());
        log.info("Server date: " + dateFormat.format(dateFromServer));
        DATE_DIFFERENCE_BETWEN_SERVER_AND_CLIENT_MILISECONDS = new Date().getTime() - (dateFromServer.getTime());
        log.info("Date after synch: " + dateFormat.format(DateTimeUtils.getCurrentServerDate()));
    }

    public void setOrderStrategy(OrderStrategy strategy) {
        orderStrategyContext.setOrderStrategy(strategy);
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
            orderService.saveAll(activeOrders);
        }
        log.info("Handle ACTIVE orders finished!");

        if (checkProfits) {
            checkProfits();
        }
    }

    public void handleOpenOrders() {
        List<Order> activeOrders = orderService.getAllOpenButNotActive();

        log.info("Handle OPEN orders start > " + activeOrders.size() + " active orders");

        for (Order order : activeOrders) {
            if (orderStrategyContext.closeNonActiveOpenOrder(order)) {
                log.debug("Close order id: " + order.getId() + " symbol: " + order.getSymbol());
                order.setOpen(false);
            }
        }

        log.info("Handle OPEN orders finished!");

        orderService.saveAll(activeOrders);
    }

    public void checkProfits() {
        List<Order> finishedOrders = (List<Order>) orderRepository.findAllByActiveFalseAndSellPriceForOrderWithFeeIsNotNull();

        double profit = 0;

        for (Order order : finishedOrders) {
            profit += order.getProfitFeeIncluded();
        }

        log.info("=================================== FINAL PROFIT: " + String.format("%.9f", (profit)) + " $$$ ===================================");

        IOUtil.saveOrderToCsv(new ArrayList<>(finishedOrders), propertyPlaceholder.getCsvReportFilePath(), false, propertyPlaceholder.getWhiteListCoins());
    }

    public boolean isMutex() {
        return mutex;
    }

    public void setMutex(boolean mutex) {
        this.mutex = mutex;
    }
}
