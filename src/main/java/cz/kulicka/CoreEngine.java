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
import cz.kulicka.util.MathUtil;
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

    boolean timerLock = false;

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

    public void handleActiveOrders(boolean instaSell) {
        List<Order> activeOrders = orderService.getAllActive();
        double actualBTCUSDT = Double.parseDouble(binanceApiService.getLastPrice(CurrenciesConstants.BTCUSDT).getPrice());
        boolean endOrder;
        boolean checkProfits = false;

        log.info("Handle orders start > " + activeOrders.size() + " active orders");

        for (Order order : activeOrders) {
            //Sell by strategy?
            double actualSellPriceBTCForUnit = Double.parseDouble(binanceApiService.getLastPrice(order.getSymbol()).getPrice());
            double actualSellPriceForOrderWithFee = MathUtil.getSellPriceForOrderWithFee(order.getBoughtAmount(),
                    actualSellPriceBTCForUnit * actualBTCUSDT, order.getSellFeeConstant());

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
                order.setSellPriceBTCForUnit(actualSellPriceBTCForUnit);
                log.info("Order STOPPED : " + order.toString());
                checkProfits = true;
            } else {
                if (!instaSell) {
                    log.info("Continuing order: " + order.toString());
                }
            }
            orderService.saveAll(activeOrders);
        }

        if (checkProfits) {
            checkProfits();
        }
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

    public boolean isTimerLock() {
        return timerLock;
    }

    public void setTimerLock(boolean timerLock) {
        this.timerLock = timerLock;
    }
}
