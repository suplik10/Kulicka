package cz.kulicka.services;

import cz.kulicka.entities.Candlestick;
import cz.kulicka.entities.CandlestickInterval;
import cz.kulicka.entities.TickerPrice;

import java.util.ArrayList;
import java.util.List;

public interface BinanceApiService {

    ArrayList<String> checkActualCurrencies(ArrayList newCurrencies);

    List<Candlestick> getCandlestickBars(String symbol, CandlestickInterval interval, Integer limit, Long startTime, Long endTime);

    List<Candlestick> getCandlestickBars(String symbol, CandlestickInterval interval);

    List<Candlestick> getCandlestickBars(String symbol, CandlestickInterval interval, Integer limit);

    List<TickerPrice> getAllPrices();

}
