package cz.kulicka.services.impl;

import cz.kulicka.entities.BookTicker;
import cz.kulicka.entities.Candlestick;
import cz.kulicka.entities.TickerPrice;
import cz.kulicka.services.WebApiService;
import org.apache.log4j.Logger;
import retrofit2.Call;

import java.util.List;

public class BinanceApiServiceImpl implements WebApiService {

    static Logger log = Logger.getLogger(BinanceApiServiceImpl.class);


    @Override
    public Call<List<BookTicker>> getBookTickers() {
        return null;
    }

    @Override
    public Call<List<Candlestick>> getCandlestickBars(String symbol, String interval, Integer limit, Long startTime, Long endTime) {
        return null;
    }

    @Override
    public Call<List<TickerPrice>> getLatestPrices() {
        return null;
    }
}
