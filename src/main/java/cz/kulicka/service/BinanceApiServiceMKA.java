package cz.kulicka.service;

import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.binance.api.client.domain.account.Order;
import com.binance.api.client.domain.account.request.CancelOrderRequest;
import com.binance.api.client.domain.account.request.OrderRequest;
import com.binance.api.client.domain.account.request.OrderStatusRequest;
import com.binance.api.client.domain.general.ExchangeInfo;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.TickerPrice;
import com.binance.api.client.domain.market.TickerStatistics;
import cz.kulicka.entity.Ticker;

import java.util.ArrayList;
import java.util.List;

public interface BinanceApiServiceMKA {

	Long getServerTime();

	ExchangeInfo getExchangeInfo();

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

	Account getAccount(Long recvWindow, Long timestamp);

}
