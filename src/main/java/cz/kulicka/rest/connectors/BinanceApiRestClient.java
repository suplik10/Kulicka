package cz.kulicka.rest.connectors;

import cz.kulicka.entities.*;
import cz.kulicka.enums.CandlestickInterval;
import cz.kulicka.entities.request.CancelOrderRequest;
import cz.kulicka.entities.request.OrderStatusRequest;

import java.util.List;

public interface BinanceApiRestClient {

    List<BookTicker> getBookTickers();

    List<Candlestick> getCandlestickBars(String symbol, CandlestickInterval interval, Integer limit, Long startTime, Long endTime);

    List<Candlestick> getCandlestickBars(String symbol, CandlestickInterval interval);

    List<Candlestick> getCandlestickBars(String symbol, CandlestickInterval interval, Integer limit);

    TickerPrice getLastPrice(String symbol);

    List<TickerPrice> getLastPrices();

    NewOrderResponse newOrder(NewOrder order);

    void newOrderTest(NewOrder order);

    Order getOrderStatus(OrderStatusRequest orderStatusRequest);

    void cancelOrder(CancelOrderRequest cancelOrderRequest);

    List<Order> getOpenOrders(OrderRequest orderRequest);

    TickerStatistics get24HrPriceStatistics(String symbol);
}
