package cz.kulicka.test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.kulicka.constant.CurrenciesConstants;
import cz.kulicka.entity.BookTicker;
import cz.kulicka.entity.Ticker;
import cz.kulicka.service.impl.BinanceApiServiceImpl;
import cz.kulicka.util.CommonUtil;
import org.junit.Assert;
import org.junit.Test;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BookTickerTest {

    @Test
    public void allPriceJsonMapper() throws IOException {

        File jsonFile = new File("src/test/resources/allBookTickers.json");

        ObjectMapper objectMapper = new ObjectMapper();

        List<BookTicker> newBookTickers = null;
        ArrayList<Ticker> newCurrencies = new ArrayList<>();
        ArrayList<Ticker> tickersDB = new ArrayList<>();
        try {
            newBookTickers = objectMapper.readValue(jsonFile, new TypeReference<List<BookTicker>>(){});

            //tickersDB = getEmptyTickers();
           tickersDB = getTickers();

            if (newBookTickers != null) {
                //tickerRepository.deleteAll();
                for (int i = 0; i < newBookTickers.size(); i++) {
                    if (newBookTickers.get(i).getSymbol().contains(CurrenciesConstants.BTC)) {
                        if (CommonUtil.addTickerToDBList(tickersDB, newBookTickers.get(i).getSymbol(), getBlackListCoins(), false, getWhiteListCOins(), true)) {
                            Ticker ticker = new Ticker(newBookTickers.get(i).getSymbol());
                            newCurrencies.add(ticker);
                            tickersDB.add(ticker);
                        }
                    }
                }
            }
        } catch (JsonMappingException e) {
            e.printStackTrace();
        }


        String tickers = "";
        for(Ticker ticker : tickersDB){
            tickers += ticker.getSymbol() + ",";
        }

        System.out.println(tickers);


    }

    private ArrayList<Ticker> getEmptyTickers(){
        return new ArrayList<>();
    }

    private ArrayList<Ticker> getTickers(){
        ArrayList<Ticker> bookTickers = new ArrayList<>();
        bookTickers.add(new Ticker("ETHBTC"));
        bookTickers.add(new Ticker("LTCBTC"));
        bookTickers.add(new Ticker("BNBBTC"));
        bookTickers.add(new Ticker("NEOBTC"));
        bookTickers.add(new Ticker("BCCBTC"));
        bookTickers.add(new Ticker("GASBTC"));
        bookTickers.add(new Ticker("BTCUSDT"));

        return bookTickers;
    }

    private List<String> getBlackListCoins(){
        List<String> bookTickers = new ArrayList<>();
        bookTickers.add("QTUMBTC");
        bookTickers.add("YOYOBTC");
        return bookTickers;
    }

    private List<String> getWhiteListCOins(){
        List<String> bookTickers = new ArrayList<>();
        bookTickers.add("ETHBTC");
        bookTickers.add("IOTABTC");
        return bookTickers;
    }


    //ETHBTC,LTCBTC,BNBBTC,NEOBTC,BCCBTC,GASBTC,BTCUSDT,HSRBTC,MCOBTC,WTCBTC,LRCBTC,QTUMBTC,YOYOBTC,OMGBTC,ZRXBTC,STRATBTC,SNGLSBTC,BQXBTC,KNCBTC,FUNBTC,SNMBTC,IOTABTC,LINKBTC,XVGBTC,SALTBTC,MDABTC,MTLBTC,SUBBTC
}
