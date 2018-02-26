package cz.kulicka;

import cz.kulicka.constant.CurrenciesConstants;
import cz.kulicka.entity.Order;
import cz.kulicka.entity.Ticker;
import cz.kulicka.exception.BinanceApiException;
import cz.kulicka.repository.OrderRepository;
import cz.kulicka.services.BinanceApiService;
import cz.kulicka.services.OrderService;
import cz.kulicka.strategy.OrderStrategyContext;
import cz.kulicka.strategy.impl.MacdStrategy;
import cz.kulicka.strategy.impl.SecondDumbStrategyImpl;
import cz.kulicka.utils.MathUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class CoreEngine {

    static Logger log = Logger.getLogger(CoreEngine.class);

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

    public void runIt() {

        setOrderStrategy();

        while (true) {

            try {
                setOrderStrategy();
                handleActiveOrders();
                checkProfits();
                scanCurrenciesAndMakeNewOrders();
            } catch (BinanceApiException e) {
                log.error("BINANCE API EXCEPTION !!!  " + e.getMessage());
                sleep();
            }

            log.info("Fall into wonderland...");
            sleep();
        }
    }

    private void setOrderStrategy() {
        orderStrategyContext.setOrderStrategy(new MacdStrategy(binanceApiService));
    }

    private void sleep() {
        try {
            Thread.sleep(propertyPlaceholder.getThreadSleepBetweenRequestsMiliseconds());
        } catch (InterruptedException e) {
            log.error("THREAD SLEEP ERROR " + e.getMessage());
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
                if (orderStrategyContext.buy(currencies.get(i), activeOrders)) {
                    double lastPriceInUSDT = Double.parseDouble(binanceApiService.getLastPrice(currencies.get(i).getSymbol()).getPrice()) * actualBTCUSDT;
                    Order newOrder = new Order(currencies.get(i).getSymbol(), new Date().getTime(), propertyPlaceholder.getPricePerOrderUSD(), lastPriceInUSDT,
                            propertyPlaceholder.getTradeBuyFee(), propertyPlaceholder.getTradeSellFee());
                    newOrder.setActive(true);
                    newOrder.setRiskValue(2);
                    orderService.create(newOrder);
                }
            }

        }
        log.info("SCAN COMPLETE!");
    }

    private void handleActiveOrders() {
        List<Order> activeOrders = orderService.getAllActive();
        double actualBTCUSDT = Double.parseDouble(binanceApiService.getLastPrice(CurrenciesConstants.BTCUSDT).getPrice());

        log.info("Handle orders start > " + activeOrders.size() + " active orders");

        for (Order order : activeOrders) {
            //Sell???
            double actualSellPriceForOrderWithFee = MathUtil.getSellPriceForOrderWithFee(order.getBoughtAmount(),
                    Double.parseDouble(binanceApiService.getLastPrice(order.getSymbol()).getPrice()) * actualBTCUSDT, order.getSellFeeConstant());
            if (orderStrategyContext.sell(order, actualSellPriceForOrderWithFee)) {
                order.setActive(false);
                order.setSellPriceForOrderWithFee(actualSellPriceForOrderWithFee);
                order.setProfitFeeIncluded(order.getSellPriceForOrderWithFee() - order.getBuyPriceForOrderWithFee());
                order.setSellTime(new Date().getTime());
                log.info("Order STOPPED : " + order.toString());
            } else {
                log.info("Continuing order: " + order.toString());
            }
        }

        orderService.saveAll(activeOrders);
    }

    private void checkProfits() {
        List<Order> finishedOrders = (List<Order>) orderRepository.findAllByActiveFalseAndSellPriceForOrderWithFeeIsNotNull();

        double profit = 0;

        for (Order order : finishedOrders) {
            profit += order.getProfitFeeIncluded();
        }

        log.info("=================================== FINAL PROFIT: " + String.format("%.9f", (profit)) + " $$$ ===================================");
    }
}
