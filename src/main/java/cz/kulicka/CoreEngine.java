package cz.kulicka;

import cz.kulicka.constant.CurrenciesConstants;
import cz.kulicka.entities.Candlestick;
import cz.kulicka.entities.Order;
import cz.kulicka.entities.TickerPrice;
import cz.kulicka.enums.CandlestickInterval;
import cz.kulicka.services.BinanceApiService;
import cz.kulicka.services.OrderService;
import cz.kulicka.services.impl.BinanceApiServiceImpl;
import cz.kulicka.services.impl.OrderServiceImpl;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class CoreEngine {

    @Autowired
    OrderService orderService;

    static Logger log = Logger.getLogger(CoreEngine.class);

    BinanceApiService binanceApiService = new BinanceApiServiceImpl();

    public void run() {
        Order order = new Order("joohohoo", 2222.5);

        orderService.create(order);

        Order order1 = orderService.getOrderById(1);
    }

    public void runIt(){
        ArrayList<String> newCurrencies = new ArrayList<>();
        ArrayList<String> currencies;
        boolean createOrder = false;


        currencies = binanceApiService.checkActualCurrencies(newCurrencies);

        //log new currencies
        if(newCurrencies != null && newCurrencies.size() > 0){
            log.warn("NEW currencies available: " + newCurrencies.toString());
            //TODO handle new currencies
        }


        if(currencies != null){
            for (int i = 0; i < currencies.size(); i++) {
                //TODO create method
                List<Candlestick> candlestickList = binanceApiService.getCandlestickBars(currencies.get(i).concat(CurrenciesConstants.BTC), CandlestickInterval.FIVE_MINUTES, 4);
                log.info("Currency " + currencies.get(i).concat(CurrenciesConstants.BTC));
                for (int y = 0; y < candlestickList.size()-1; y++) {
                    log.info(new Date(candlestickList.get(y).getOpenTime()) + " open value "  + candlestickList.get(y).getOpen() + " close value: " + candlestickList.get(y).getClose());

                    if(Double.parseDouble(candlestickList.get(y).getClose()) > Double.parseDouble(candlestickList.get(y).getOpen())){
                        createOrder = true;
                    }else{
                        createOrder = false;
                        break;
                    }


                }

                TickerPrice tickerPrice = binanceApiService.getLastPrice(currencies.get(i).concat(CurrenciesConstants.BTC));


                if(createOrder){
                    log.info("Currency " + currencies.get(i).concat(CurrenciesConstants.BTC) + " [[[[MAKE ORDER " + createOrder + " ]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]");
                }


            }
            log.info("SCAN COMPLETE!");
        }







    }


}
