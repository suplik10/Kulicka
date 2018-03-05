package cz.kulicka;

import cz.kulicka.constant.CurrenciesConstants;
import cz.kulicka.entity.Order;
import cz.kulicka.entity.Ticker;
import cz.kulicka.exception.BinanceApiException;
import cz.kulicka.repository.OrderRepository;
import cz.kulicka.service.BinanceApiService;
import cz.kulicka.service.MacdIndicatorService;
import cz.kulicka.service.OrderService;
import cz.kulicka.strategy.OrderStrategyContext;
import cz.kulicka.strategy.impl.MacdStrategyImpl;
import cz.kulicka.util.DateTimeUtils;
import cz.kulicka.util.IOUtil;
import cz.kulicka.util.MathUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class CoreEngine {

    static Logger log = Logger.getLogger(CoreEngine.class);

    public static Long DATE_DIFFERENCE_BETWEN_SERVER_AND_CLIENT;

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

    public void runIt() {

        synchronizeServerTime();
        setOrderStrategy();

        Calendar actualDate = Calendar.getInstance();
        actualDate.setTime(new Date());
        int dayOfWeek = actualDate.get(Calendar.DAY_OF_WEEK);


        Calendar calendar = Calendar.getInstance();
        calendar.set(
                Calendar.DAY_OF_WEEK,
                actualDate.get(Calendar.DAY_OF_WEEK)
        );
        calendar.set(Calendar.HOUR_OF_DAY, actualDate.get(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, actualDate.get(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, actualDate.get(Calendar.SECOND));

        Timer time = new Timer(); // Instantiate Timer Object

        // Start running the task on Monday at 15:40:00, period is set to 8 hours
        // if you want to run the task immediately, set the 2nd parameter to 0
        time.schedule(new TimerTest(), calendar.getTime(), TimeUnit.MINUTES.toMillis(5));



            try {
                if (2 < propertyPlaceholder.getThreadSleepBetweenRequestsMinutes()) {
                    log.info("------ RUN INSTASELL ------");
                    handleActiveOrders(true);
                    checkProfits();
                    //countMinutes++;
                } else {
                    log.info("------ RUN MAIN STRATEGY ------");
                    handleActiveOrders(false);
                    checkProfits();
                    scanCurrenciesAndMakeNewOrders();
                    //countMinutes = 1;
                }
            } catch (BinanceApiException e) {
                log.error("BINANCE API EXCEPTION !!!  " + e.getMessage());
                sleepInstaSellThread();
            }

    }

    private void synchronizeServerTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        log.info("------ Date synch ------");
        log.info("Date before synch: " + dateFormat.format(new Date()));
        Date dateFromServer = new Date(binanceApiService.getServerTime());
        log.info("Server date: " + dateFormat.format(dateFromServer));
        DATE_DIFFERENCE_BETWEN_SERVER_AND_CLIENT = new Date().getTime() - (dateFromServer.getTime());
        log.info("Date after synch: " + dateFormat.format(DateTimeUtils.getCurrentServerDate()));
    }

    private void setOrderStrategy() {
        orderStrategyContext.setOrderStrategy(new MacdStrategyImpl(binanceApiService, macdIndicatorService, orderService, propertyPlaceholder));
    }

    private void sleep() {
        try {
            Thread.sleep(propertyPlaceholder.getThreadSleepBetweenRequestsMinutes() * 60 * 1000);
        } catch (InterruptedException e) {
            log.error("THREAD SLEEP ERROR " + e.getMessage());
        }
    }

    private void sleepInstaSellThread() {
        try {
            Thread.sleep(propertyPlaceholder.getThreadSleepBetweenRequestsInstaSellMiliseconds());
        } catch (InterruptedException e) {
            log.error("THREAD SLEEP INSTASELL ERROR " + e.getMessage());
        }
    }

    private void scanCurrenciesAndMakeNewOrders() {
        ArrayList<Ticker> newCurrencies = new ArrayList<>();
        List<Order> activeOrders = (List<Order>) orderRepository.findAllByActiveTrue();
        ArrayList<Ticker> currencies;
        log.info("SCAN START!");

        currencies = binanceApiService.checkActualCurrencies(newCurrencies);
        double actualBTCUSDT = Double.parseDouble(binanceApiService.getLastPrice(CurrenciesConstants.BTCUSDT).getPrice());

        if (currencies != null) {
            for (int i = 0; i < currencies.size(); i++) {
                //Buy???
                if (orderStrategyContext.buy(currencies.get(i), activeOrders, actualBTCUSDT)) {
                    //TODO handle that!
                    log.debug("buy no!");
                }
            }

        }
        log.info("SCAN COMPLETE!");
    }

    private void handleActiveOrders(boolean instaSell) {
        List<Order> activeOrders = orderService.getAllActive();
        double actualBTCUSDT = Double.parseDouble(binanceApiService.getLastPrice(CurrenciesConstants.BTCUSDT).getPrice());
        boolean endOrder;

        log.info("Handle orders start > " + activeOrders.size() + " active orders");

        for (Order order : activeOrders) {
            //Sell by strategy?
            double actualSellPriceForOrderWithFee = MathUtil.getSellPriceForOrderWithFee(order.getBoughtAmount(),
                    Double.parseDouble(binanceApiService.getLastPrice(order.getSymbol()).getPrice()) * actualBTCUSDT, order.getSellFeeConstant());

            if (instaSell) {
                endOrder = orderStrategyContext.instaSellForProfit(order, actualSellPriceForOrderWithFee);
            } else {
                endOrder = orderStrategyContext.sell(order, actualSellPriceForOrderWithFee);
            }

            if (endOrder) {
                order.setActive(false);
                order.setSellPriceForOrderWithFee(actualSellPriceForOrderWithFee);
                order.setProfitFeeIncluded(order.getSellPriceForOrderWithFee() - order.getBuyPriceForOrderWithFee());
                order.setSellTime(DateTimeUtils.getCurrentServerDate().getTime());
                log.info("Order STOPPED : " + order.toString());
            } else {
                if(!instaSell){
                    log.info("Continuing order: " + order.toString());
                }
            }
            orderService.saveAll(activeOrders);
        }
    }

    private void checkProfits() {
        List<Order> finishedOrders = (List<Order>) orderRepository.findAllByActiveFalseAndSellPriceForOrderWithFeeIsNotNull();

        double profit = 0;

        for (Order order : finishedOrders) {
            profit += order.getProfitFeeIncluded();
        }

        log.info("=================================== FINAL PROFIT: " + String.format("%.9f", (profit)) + " $$$ ===================================");

        IOUtil.saveOrderToCsv(new ArrayList<>(finishedOrders), propertyPlaceholder.getCsvReportFilePath(), false);
    }
}
