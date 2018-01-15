package cz.kulicka.rest.connectors;

import cz.kulicka.entities.BookTicker;
import cz.kulicka.entities.Candlestick;
import cz.kulicka.entities.CandlestickInterval;
import cz.kulicka.entities.TickerPrice;

import java.util.List;

public interface BinanceApiRestClient {

    List<BookTicker> getBookTickers();

    List<Candlestick> getCandlestickBars(String symbol, CandlestickInterval interval, Integer limit, Long startTime, Long endTime);

    List<Candlestick> getCandlestickBars(String symbol, CandlestickInterval interval);

    List<Candlestick> getCandlestickBars(String symbol, CandlestickInterval interval, Integer limit);

    List<TickerPrice> getAllPrices();
}
