package cz.kulicka;

import cz.kulicka.constant.CurrenciesConstants;
import cz.kulicka.entities.Candlestick;
import cz.kulicka.entities.CandlestickInterval;
import cz.kulicka.services.CurrencyService;
import cz.kulicka.services.impl.CurrencyServiceImpl;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CoreEngine {


    static Logger log = Logger.getLogger(CoreEngine.class);

    CurrencyService currencyService = new CurrencyServiceImpl();



    public void runIt(){
        ArrayList<String> newCurrencies = new ArrayList<>();
        ArrayList<String> currencies;
        boolean createOrder = false;


        currencies = currencyService.checkActualCurrencies(newCurrencies);

        //log new currencies
        if(newCurrencies != null && newCurrencies.size() > 0){
            log.warn("NEW currencies available: " + newCurrencies.toString());
            //TODO handle new currencies
        }


        if(currencies != null){
            for (int i = 0; i < currencies.size(); i++) {
                //TODO create method
                List<Candlestick> candlestickList = currencyService.getCandlestickBars(currencies.get(i).concat(CurrenciesConstants.BTC), CandlestickInterval.FIVE_MINUTES, 4);
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

                if(createOrder){
                    log.info("Currency " + currencies.get(i).concat(CurrenciesConstants.BTC) + " [[[[MAKE ORDER " + createOrder + " ]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]");
                }


            }
            log.info("SCAN COMPLETE!");
        }







    }
}
