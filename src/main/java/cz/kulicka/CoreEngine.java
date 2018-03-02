package cz.kulicka;

import cz.kulicka.constant.CurrenciesConstants;
import cz.kulicka.entity.Order;
import cz.kulicka.entity.Ticker;
import cz.kulicka.exception.BinanceApiException;
import cz.kulicka.repository.OrderRepository;
import cz.kulicka.services.BinanceApiService;
import cz.kulicka.services.MacdIndicatorService;
import cz.kulicka.services.OrderService;
import cz.kulicka.strategy.OrderStrategyContext;
import cz.kulicka.strategy.impl.MacdStrategyImpl;
import cz.kulicka.utils.MathUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
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
    MacdIndicatorService macdIndicatorService;


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
        orderStrategyContext.setOrderStrategy(new MacdStrategyImpl(binanceApiService, macdIndicatorService, orderService, propertyPlaceholder));
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
                if (orderStrategyContext.buy(currencies.get(i), activeOrders, actualBTCUSDT)) {
                    //TODO handle that!
                    log.debug("buy no!");
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

    private void reportToCsv() throws IOException {
        String csvFile = "/Users/mkyong/csv/developer.csv";
        FileWriter writer = new FileWriter(csvFile);

        //for header
        CSVUtils.writeLine(writer, Arrays.asList("Name", "Salary", "Age"));

        for (Developer d : developers) {

            List<String> list = new ArrayList<>();
            list.add(d.getName());
            list.add(d.getSalary().toString());
            list.add(String.valueOf(d.getAge()));

            CSVUtils.writeLine(writer, list);

            //try custom separator and quote.
            //CSVUtils.writeLine(writer, list, '|', '\"');
        }

        writer.flush();
        writer.close();
    }
}
