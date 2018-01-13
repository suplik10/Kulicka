package cz.kulicka.services.impl;

import cz.kulicka.BinanceApiClientFactory;
import cz.kulicka.constant.AppConstants;
import cz.kulicka.constant.CurrenciesConstants;
import cz.kulicka.entities.BookTicker;
import cz.kulicka.entities.Candlestick;
import cz.kulicka.entities.CandlestickInterval;
import cz.kulicka.rest.connectors.BinanceApiRestClient;
import cz.kulicka.services.CurrencyService;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static cz.kulicka.utils.IOUtil.loadListOfStringsToFile;
import static cz.kulicka.utils.IOUtil.saveListOfStringsToFile;

public class CurrencyServiceImpl implements CurrencyService {

    static Logger log = Logger.getLogger(CurrencyServiceImpl.class);

    @Override
    public ArrayList<String> checkActualCurrencies(ArrayList newCurrencies) {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        BinanceApiRestClient client = factory.newRestClient();

        List<BookTicker> bookTickers = client.getBookTickers();

        ArrayList<String> apiList = new ArrayList<>();
        ArrayList<String> fileList = loadListOfStringsToFile(AppConstants.CURRENCY_LIST_FILE_PATH);

        if (bookTickers != null) {
            for (int i = 0; i < bookTickers.size(); i++) {
                if (bookTickers.get(i).getSymbol().contains(CurrenciesConstants.BTC)) {
                    apiList.add(bookTickers.get(i).getSymbol().replace(CurrenciesConstants.BTC, ""));
                }
            }
        }

        if (apiList == null) {
            log.warn("Api /api/v1/ticker/allBookTickers returned null list!");
            if (fileList == null) {
                log.warn("Saved fileList is null too! - returning null list of currencies");
            }
            return fileList;
        }

        //remove blacklist currencies
        apiList.removeAll(CurrenciesConstants.BLACK_LIST);
        log.info("Currencies to save: " + apiList.toString());

        if (fileList == null) {
            saveListOfStringsToFile(apiList, AppConstants.CURRENCY_LIST_FILE_PATH);
            return apiList;
        }

        saveListOfStringsToFile(apiList, AppConstants.CURRENCY_LIST_FILE_PATH);

        ArrayList<String> copyOfApiList = (ArrayList<String>) apiList.clone();
        copyOfApiList.removeAll(fileList);
        newCurrencies.addAll(copyOfApiList);

        log.info("New currencies on exchange! : " + newCurrencies.toString());

        return apiList;
    }

    @Override
    public List<Candlestick> getCandlestickBars(String symbol, CandlestickInterval interval, Integer limit, Long startTime, Long endTime) {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        BinanceApiRestClient client = factory.newRestClient();

        return client.getCandlestickBars(symbol, interval, limit, startTime, endTime);
    }

    @Override
    public List<Candlestick> getCandlestickBars(String symbol, CandlestickInterval interval) {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        BinanceApiRestClient client = factory.newRestClient();

        return client.getCandlestickBars(symbol, interval);
    }

    @Override
    public List<Candlestick> getCandlestickBars(String symbol, CandlestickInterval interval, Integer limit) {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        BinanceApiRestClient client = factory.newRestClient();

        return client.getCandlestickBars(symbol, interval, limit);
    }
}
