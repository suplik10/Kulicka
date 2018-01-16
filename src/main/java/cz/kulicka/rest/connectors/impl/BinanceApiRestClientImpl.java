package cz.kulicka.rest.connectors.impl;

import cz.kulicka.entities.BookTicker;
import cz.kulicka.entities.Candlestick;
import cz.kulicka.enums.CandlestickInterval;
import cz.kulicka.entities.NewOrder;
import cz.kulicka.entities.NewOrderResponse;
import cz.kulicka.entities.Order;
import cz.kulicka.entities.OrderRequest;
import cz.kulicka.entities.TickerPrice;
import cz.kulicka.entities.request.CancelOrderRequest;
import cz.kulicka.entities.request.OrderStatusRequest;
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

    @Override
    public TickerPrice getLastPrice(String symbol) {
        return executeSync(binanceApiService.getLatestPrice(symbol));
    }

    @Override
    public List<TickerPrice> getLastPrices() {
        return executeSync(binanceApiService.getLatestPrices());
    }

    @Override
    public NewOrderResponse newOrder(NewOrder order) {
        return executeSync(binanceApiService.newOrder(order.getSymbol(), order.getSide(), order.getType(),
                order.getTimeInForce(), order.getQuantity(), order.getPrice(), order.getStopPrice(), order.getIcebergQty(),
                order.getRecvWindow(), order.getTimestamp()));
    }

    @Override
    public void newOrderTest(NewOrder order) {
        executeSync(binanceApiService.newOrderTest(order.getSymbol(), order.getSide(), order.getType(),
                order.getTimeInForce(), order.getQuantity(), order.getPrice(), order.getStopPrice(), order.getIcebergQty(),
                order.getRecvWindow(), order.getTimestamp()));
    }

    // Account endpoints

    @Override
    public Order getOrderStatus(OrderStatusRequest orderStatusRequest) {
        return executeSync(binanceApiService.getOrderStatus(orderStatusRequest.getSymbol(),
                orderStatusRequest.getOrderId(), orderStatusRequest.getOrigClientOrderId(),
                orderStatusRequest.getRecvWindow(), orderStatusRequest.getTimestamp()));
    }

    @Override
    public void cancelOrder(CancelOrderRequest cancelOrderRequest) {
        executeSync(binanceApiService.cancelOrder(cancelOrderRequest.getSymbol(),
                cancelOrderRequest.getOrderId(), cancelOrderRequest.getOrigClientOrderId(), cancelOrderRequest.getNewClientOrderId(),
                cancelOrderRequest.getRecvWindow(), cancelOrderRequest.getTimestamp()));
    }

    @Override
    public List<Order> getOpenOrders(OrderRequest orderRequest) {
        return executeSync(binanceApiService.getOpenOrders(orderRequest.getSymbol(), orderRequest.getRecvWindow(), orderRequest.getTimestamp()));
    }
}
