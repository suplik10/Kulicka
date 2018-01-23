package cz.kulicka;

import cz.kulicka.entities.Order;
import cz.kulicka.entities.Ticker;
import cz.kulicka.entities.TickerStatistics;
import cz.kulicka.exception.BinanceApiException;
import cz.kulicka.repository.OrderRepository;
import cz.kulicka.services.BinanceApiService;
import cz.kulicka.services.OrderService;
import cz.kulicka.services.OrderStrategyService;
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
    PropertyPlacehoder propertyPlacehoder;

    @Autowired
    OrderStrategyService orderStrategyService;


    public void run() {
        //checkProfits();
        //runIt();

        TickerStatistics tickerStatistics = binanceApiService.get24HrPriceStatistics("LTCBTC");
    }

    private void checkProfits() {
        List<Order> finishedOrders = (List<Order>) orderRepository.findAllByActiveFalseAndSellPriceIsNotNull();

        double profit = 0;

        for (Order order : finishedOrders) {
            profit += order.getProfit();
        }

        log.info("FINAL PROFIT: " + String.format("%.9f", (profit)));
    }

    public void runIt() {

        while (true) {

            try {
                handleActiveOrders();
                scanCurrenciesAndMakeNewOrders();
            } catch (BinanceApiException e) {
                log.error("BINANCE API EXCEPTION !!!  " + e.getMessage());
                sleep();
            }

            log.info("Going sleep :-))))");
            sleep();
        }
    }

    private void sleep() {
        try {
            Thread.sleep(propertyPlacehoder.getThreadSleepBetweenRequestsMiliseconds());
        } catch (InterruptedException e) {
            log.error("THREAD SLEEP ERROR " + e.getMessage());
        }
    }

    private void scanCurrenciesAndMakeNewOrders() {
        ArrayList<Ticker> newCurrencies = new ArrayList<>();
        ArrayList<Ticker> currencies;
        log.info("SCAN START!");

        currencies = binanceApiService.checkActualCurrencies(newCurrencies);

        if (currencies != null) {
            for (int i = 0; i < currencies.size(); i++) {
                //Buy???
                if (orderStrategyService.firstDumbBuyStrategy(currencies.get(i))) {
                    Order newOrder = new Order(currencies.get(i).getSymbol(), Double.parseDouble(binanceApiService.getLastPrice(currencies.get(i).getSymbol()).getPrice()), new Date().getTime());
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

        log.info("Handle orders start> " + activeOrders.size() + " active orders");

        for (Order order : activeOrders) {
            //Sell???
            if (orderStrategyService.secondDumbSellStrategyWithStopLoss(order)) {
                order.setActive(false);
                order.setSellPrice(Double.parseDouble(binanceApiService.getLastPrice(order.getSymbol()).getPrice()));
                order.setProfit(order.getSellPrice() - order.getBuyPrice());
                order.setSellTime(new Date().getTime());
                log.info("Order stopped id: " + order.getId() + "Profit: " + String.format("%.9f", order.getProfit()));
            }
        }

        orderService.saveAll(activeOrders);
    }
}
