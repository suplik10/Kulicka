package cz.kulicka.services;

import cz.kulicka.entity.*;
import cz.kulicka.entity.request.CancelOrderRequest;
import cz.kulicka.entity.request.OrderStatusRequest;

import java.util.ArrayList;
import java.util.List;

public interface BinanceApiService {

    ArrayList<Ticker> checkActualCurrencies(ArrayList newCurrencies);

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
