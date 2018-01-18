package cz.kulicka;

import cz.kulicka.entities.Candlestick;
import cz.kulicka.entities.Order;
import cz.kulicka.entities.Ticker;
import cz.kulicka.enums.CandlestickInterval;
import cz.kulicka.repository.OrderRepository;
import cz.kulicka.services.BinanceApiService;
import cz.kulicka.services.OrderService;
import cz.kulicka.services.impl.BinanceApiServiceImpl;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class CoreEngine {

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    BinanceApiService  binanceApiService;

    static Logger log = Logger.getLogger(CoreEngine.class);

    public void run() {
        runIt();

    }

    public void runIt() {
        ArrayList<Ticker> newCurrencies = new ArrayList<>();
        ArrayList<Ticker> currencies;
        boolean createOrder = false;


        currencies = binanceApiService.checkActualCurrencies(newCurrencies);

        //log new currencies
        if (newCurrencies != null && newCurrencies.size() > 0) {
            log.warn("NEW currencies available: " + newCurrencies.toString());
            //TODO handle new currencies
        }


        if (currencies != null) {
            for (int i = 0; i < currencies.size(); i++) {
                //TODO create method
                List<Candlestick> candlestickList = binanceApiService.getCandlestickBars(currencies.get(i).getSymbol(), CandlestickInterval.FIVE_MINUTES, 4);
                log.info("Currency " + currencies.get(i).getSymbol());
                for (int y = 0; y < candlestickList.size() - 1; y++) {
                    log.info(new Date(candlestickList.get(y).getOpenTime()) + " open value " + candlestickList.get(y).getOpen() + " close value: " + candlestickList.get(y).getClose());

                    if (Double.parseDouble(candlestickList.get(y).getClose()) > Double.parseDouble(candlestickList.get(y).getOpen())) {
                        createOrder = true;
                    } else {
                        createOrder = false;
                        break;
                    }
                }
                if (createOrder) {
                    log.info("Currency " + currencies.get(i).getSymbol() + " [[[[MAKE ORDER " + createOrder + " ]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]");
                }
            }
            log.info("SCAN COMPLETE!");
        }


    }


}
