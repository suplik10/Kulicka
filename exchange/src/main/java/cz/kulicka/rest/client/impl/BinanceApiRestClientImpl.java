package cz.kulicka.rest.client.impl;

import cz.kulicka.PropertyPlaceholder;
import cz.kulicka.entity.BookTicker;
import cz.kulicka.entity.Candlestick;
import cz.kulicka.entity.NewOrder;
import cz.kulicka.entity.NewOrderResponse;
import cz.kulicka.entity.Order;
import cz.kulicka.entity.OrderRequest;
import cz.kulicka.entity.TickerPrice;
import cz.kulicka.entity.TickerStatistics;
import cz.kulicka.entity.request.CancelOrderRequest;
import cz.kulicka.entity.request.OrderStatusRequest;
import cz.kulicka.rest.client.BinanceApiRestClient;
import cz.kulicka.service.WebApiService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

import static cz.kulicka.service.impl.BinanceApiServiceGenerator.createService;
import static cz.kulicka.service.impl.BinanceApiServiceGenerator.executeSync;

@Component
public class BinanceApiRestClientImpl implements BinanceApiRestClient {

    static Logger log = Logger.getLogger(BinanceApiRestClientImpl.class);

    @Autowired
    PropertyPlaceholder propertyPlaceholder;

    private WebApiService binanceApiService;

    public BinanceApiRestClientImpl() {
    }

    @PostConstruct
    private void initWebApiService() {
        binanceApiService = createService(WebApiService.class, propertyPlaceholder.getApiKey(), propertyPlaceholder.getSecret());
    }

    @Override
    public Long getServerTime() {
        return executeSync(binanceApiService.getServerTime()).getServerTime();
    }


    @Override
    public List<BookTicker> getBookTickers() {
        log.debug("getBookTickers");
        return executeSync(binanceApiService.getBookTickers());
    }

    @Override
    public List<Candlestick> getCandlestickBars(String symbol, String interval, Integer limit, Long startTime, Long endTime) {
        log.debug("getCandlestickBars( " + symbol + " " + interval + " " + limit + " " + startTime + " " + endTime + " )");
        return executeSync(binanceApiService.getCandlestickBars(symbol, interval, limit, startTime, endTime));
    }

    @Override
    public List<Candlestick> getCandlestickBars(String symbol, String interval) {
        return getCandlestickBars(symbol, interval, null, null, null);
    }

    @Override
    public List<Candlestick> getCandlestickBars(String symbol, String interval, Integer limit) {
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

    @Override
    public TickerStatistics get24HrPriceStatistics(String symbol) {
        return executeSync(binanceApiService.get24HrPriceStatistics(symbol));
    }
}
