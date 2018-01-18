package cz.kulicka;

import cz.kulicka.entities.Candlestick;
import cz.kulicka.entities.Order;
import cz.kulicka.entities.Ticker;
import cz.kulicka.enums.CandlestickInterval;
import cz.kulicka.exception.BinanceApiException;
import cz.kulicka.repository.OrderRepository;
import cz.kulicka.services.BinanceApiService;
import cz.kulicka.services.OrderService;
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


    public void run() {
        runIt();

    }

    public void runIt() {

        while(true) {

            try{
                handleActiveOrders();

                scanCurrenciesAndMakeNewOrders();
            }catch (BinanceApiException e){
                log.error("BINANCE API EXCEPTION !!!  " + e.getMessage());
                sleep();
            }

            log.info("Going sleep :-))))");
            sleep();

        }


    }

    private void sleep(){
        try {
            Thread.sleep(300000);
        } catch (InterruptedException e) {
            log.error("THREAD SLEEP ERROR " + e.getMessage());
        }
    }

    private void scanCurrenciesAndMakeNewOrders() {
        ArrayList<Ticker> newCurrencies = new ArrayList<>();
        ArrayList<Ticker> currencies;
        boolean createOrder = false;
        log.info("SCAN START!");

        currencies = binanceApiService.checkActualCurrencies(newCurrencies);

        if (currencies != null) {
            for (int i = 0; i < currencies.size(); i++) {
                //TODO create method
                List<Candlestick> candlestickList = binanceApiService.getCandlestickBars(currencies.get(i).getSymbol(), CandlestickInterval.FIVE_MINUTES, 4);
                log.debug("Currency " + currencies.get(i).getSymbol());
                for (int y = 0; y < candlestickList.size() - 1; y++) {
                    log.debug(new Date(candlestickList.get(y).getOpenTime()) + " open value " + candlestickList.get(y).getOpen() + " close value: " + candlestickList.get(y).getClose());

                    if (Double.parseDouble(candlestickList.get(y).getClose()) > Double.parseDouble(candlestickList.get(y).getOpen())) {
                        createOrder = true;
                    } else {
                        createOrder = false;
                        break;
                    }
                }
                if (createOrder) {
                    Order newOrder = new Order(currencies.get(i).getSymbol(), Double.parseDouble(binanceApiService.getLastPrice(currencies.get(i).getSymbol()).getPrice()));
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
            if (order.getRiskValue() > 1) {
                order.setRiskValue(order.getRiskValue() - 1);
                log.info("Order resuming id: " + order.getId());
            } else {
                order.setActive(false);
                order.setSellPrice(Double.parseDouble(binanceApiService.getLastPrice(order.getSymbol()).getPrice()));
                log.info("Order stoped id: " + order.getId() + "Profit: " + String.format("%.9f", (order.getSellPrice() - order.getBuyPrice())));
            }
        }

        orderService.saveAll(activeOrders);
    }


}
