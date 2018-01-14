package cz.kulicka.rest.connectors.impl;

import cz.kulicka.entities.BookTicker;
import cz.kulicka.entities.Candlestick;
import cz.kulicka.entities.CandlestickInterval;
import cz.kulicka.rest.connectors.BinanceApiRestClient;
import cz.kulicka.services.WebApiService;
import org.apache.log4j.Logger;

import java.util.List;

import static cz.kulicka.services.impl.BinanceApiServiceGenerator.createService;
import static cz.kulicka.services.impl.BinanceApiServiceGenerator.executeSync;

public class BinanceApiRestClientImpl implements BinanceApiRestClient {

    static Logger log = Logger.getLogger(BinanceApiRestClientImpl.class);

    private final WebApiService binanceApiService;

    public BinanceApiRestClientImpl(String apiKey, String secret) {
        binanceApiService = createService(WebApiService.class, apiKey, secret);
    }

    @Override
    public List<BookTicker> getBookTickers() {
        log.info("getBookTickers");
        return executeSync(binanceApiService.getBookTickers());
    }

    @Override
    public List<Candlestick> getCandlestickBars(String symbol, CandlestickInterval interval, Integer limit, Long startTime, Long endTime) {
        log.info("getCandlestickBars( " + symbol + " " + interval.getIntervalId() + " " + limit + " " + startTime + " " + endTime + " )");
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
