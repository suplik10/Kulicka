package cz.kulicka.rest.client;

import cz.kulicka.entity.*;
import cz.kulicka.entity.request.CancelOrderRequest;
import cz.kulicka.entity.request.OrderStatusRequest;

import java.util.List;

public interface BinanceApiRestClient {

    Long getServerTime();

    List<BookTicker> getBookTickers();

    List<Candlestick> getCandlestickBars(String symbol, String interval, Integer limit, Long startTime, Long endTime);

    List<Candlestick> getCandlestickBars(String symbol, String interval);

    List<Candlestick> getCandlestickBars(String symbol, String interval, Integer limit);

    TickerPrice getLastPrice(String symbol);

    List<TickerPrice> getLastPrices();

    NewOrderResponse newOrder(NewOrder order);

    void newOrderTest(NewOrder order);

    Order getOrderStatus(OrderStatusRequest orderStatusRequest);

    void cancelOrder(CancelOrderRequest cancelOrderRequest);

    List<Order> getOpenOrders(OrderRequest orderRequest);

    TickerStatistics get24HrPriceStatistics(String symbol);
}
