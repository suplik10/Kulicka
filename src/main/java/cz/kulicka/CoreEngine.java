package cz.kulicka;

import cz.kulicka.entity.Order;
import cz.kulicka.entity.Ticker;
import cz.kulicka.exception.BinanceApiException;
import cz.kulicka.repository.OrderRepository;
import cz.kulicka.rest.client.BinanceApiRestClient;
import cz.kulicka.services.BinanceApiService;
import cz.kulicka.services.OrderService;
import cz.kulicka.strategy.OrderStrategyContext;
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

    @Autowired
    BinanceApiRestClient binanceApiRestClient;


    public void run() {
        runIt();
    }


    public void runIt() {

        while (true) {

            try {
                handleActiveOrders();
                checkProfits();
                scanCurrenciesAndMakeNewOrders();
            } catch (BinanceApiException e) {
                log.error("BINANCE API EXCEPTION !!!  " + e.getMessage());
                sleep();
            }

            log.info("Fall into empire of dreams...");
            sleep();
        }
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
        double actualBTCUSDT = Double.parseDouble(binanceApiService.getLastPrice("BTCUSDT").getPrice());

        if (currencies != null) {
            for (int i = 0; i < currencies.size(); i++) {
                //Buy???
                if (orderStrategyContext.buy(currencies.get(i), activeOrders)) {
                    Order newOrder = new Order(currencies.get(i).getSymbol(), Double.parseDouble(binanceApiService.getLastPrice(currencies.get(i).getSymbol()).getPrice()) * actualBTCUSDT,
                            new Date().getTime(), 30 / (Double.parseDouble(binanceApiService.getLastPrice(currencies.get(i).getSymbol()).getPrice()) * actualBTCUSDT));
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
        double actualBTCUSDT = Double.parseDouble(binanceApiService.getLastPrice("BTCUSDT").getPrice());

        log.info("Handle orders start> " + activeOrders.size() + " active orders");

        for (Order order : activeOrders) {
            //Sell???
            if (orderStrategyContext.sell(order)) {
                order.setActive(false);
                order.setSellPrice(Double.parseDouble(binanceApiService.getLastPrice(order.getSymbol()).getPrice()) * actualBTCUSDT);
                order.setProfit(order.getSellPrice() * order.getAmount() - order.getBuyPrice() * order.getAmount());
                order.setSellTime(new Date().getTime());
                log.info("Order stopped id: " + order.getId() + "Profit: " + String.format("%.9f", order.getProfit()));
            } else {
                log.info("Continuing order id: " + order.getId() + " Symbol: " + order.getSymbol() + " Actual profit: " + String.format("%.9f", order.getStepedPrice() - order.getBuyPrice()));
            }
        }

        orderService.saveAll(activeOrders);
    }

    private void checkProfits() {
        List<Order> finishedOrders = (List<Order>) orderRepository.findAllByActiveFalseAndSellPriceIsNotNull();

        double profit = 0;

        for (Order order : finishedOrders) {
            profit += order.getProfit();
        }

        log.info("FINAL PROFIT: " + String.format("%.9f", (profit)));
    }
}
