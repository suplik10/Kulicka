package cz.kulicka.services;

import cz.kulicka.entities.Candlestick;
import cz.kulicka.entities.Ticker;
import cz.kulicka.enums.CandlestickInterval;
import cz.kulicka.entities.NewOrder;
import cz.kulicka.entities.NewOrderResponse;
import cz.kulicka.entities.Order;
import cz.kulicka.entities.OrderRequest;
import cz.kulicka.entities.TickerPrice;
import cz.kulicka.entities.request.CancelOrderRequest;
import cz.kulicka.entities.request.OrderStatusRequest;

import java.util.ArrayList;
import java.util.List;

public interface BinanceApiService {

    ArrayList<Ticker> checkActualCurrencies(ArrayList newCurrencies);

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

}
