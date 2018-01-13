package cz.kulicka.rest.connectors.impl;

import cz.kulicka.entities.BookTicker;
import cz.kulicka.entities.Candlestick;
import cz.kulicka.entities.CandlestickInterval;
import cz.kulicka.rest.connectors.BinanceApiRestClient;
import cz.kulicka.services.WebApiService;

import java.util.List;

import static cz.kulicka.services.impl.BinanceApiServiceGenerator.createService;
import static cz.kulicka.services.impl.BinanceApiServiceGenerator.executeSync;

public class BinanceApiRestClientImpl implements BinanceApiRestClient {

    private final WebApiService binanceApiService;

    public BinanceApiRestClientImpl(String apiKey, String secret) {
        binanceApiService = createService(WebApiService.class, apiKey, secret);
    }

    @Override
    public List<BookTicker> getBookTickers() {
        return executeSync(binanceApiService.getBookTickers());
    }

    @Override
    public List<Candlestick> getCandlestickBars(String symbol, CandlestickInterval interval, Integer limit, Long startTime, Long endTime) {
        return executeSync(binanceApiService.getCandlestickBars(symbol, interval.getIntervalId(), limit, startTime, endTime));
    }

    @Override
    public List<Candlestick> getCandlestickBars(String symbol, CandlestickInterval interval) {
        return getCandlestickBars(symbol, interval, null, null, null);
    }

    @Override
    public List<Candlestick> getCandlestickBars(String symbol, CandlestickInterval interval, Integer limit) {
        return getCandlestickBars(symbol, interval, limit, null, null);
    }
}
