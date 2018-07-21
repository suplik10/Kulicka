package cz.kulicka.service.impl;


import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.binance.api.client.domain.account.Order;
import com.binance.api.client.domain.account.request.CancelOrderRequest;
import com.binance.api.client.domain.account.request.OrderRequest;
import com.binance.api.client.domain.account.request.OrderStatusRequest;
import com.binance.api.client.domain.general.ExchangeInfo;
import com.binance.api.client.domain.market.BookTicker;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.TickerPrice;
import com.binance.api.client.domain.market.TickerStatistics;
import cz.kulicka.PropertyPlaceholder;
import cz.kulicka.constant.CurrenciesConstants;
import cz.kulicka.entity.Ticker;
import cz.kulicka.repository.TickerRepository;
import cz.kulicka.service.BinanceApiServiceMKA;
import cz.kulicka.util.CommonUtil;
import cz.kulicka.util.MapperUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BinanceApiServiceMKAImpl implements BinanceApiServiceMKA {

	static Logger log = Logger.getLogger(BinanceApiServiceMKAImpl.class);

	@Autowired
	TickerRepository tickerRepository;

	@Autowired
	BinanceApiRestClient client;

	@Autowired
	PropertyPlaceholder propertyPlaceholder;

	@Override
	public Long getServerTime() {
		return client.getServerTime();
	}

	@Override
	public ExchangeInfo getExchangeInfo() {
		return client.getExchangeInfo();
	}

	@Override
	public ArrayList<Ticker> checkActualCurrencies(ArrayList newCurrencies) {

		List<BookTicker> newBookTickers = client.getBookTickers();

		ArrayList<Ticker> tickersDB = (ArrayList<Ticker>) tickerRepository.findAll();

		if (newBookTickers != null) {
			tickerRepository.deleteAll();
			for (int i = 0; i < newBookTickers.size(); i++) {
				if (newBookTickers.get(i).getSymbol().contains(CurrenciesConstants.BTC)) {
					if (CommonUtil.addTickerToDBList(tickersDB, newBookTickers.get(i).getSymbol(), propertyPlaceholder.getBlackListCoins(), propertyPlaceholder.isIgnoreBlacklist(), propertyPlaceholder.getWhitelistCoins(), propertyPlaceholder.isIgnoreWhitelist())) {
						Ticker ticker = new Ticker(newBookTickers.get(i).getSymbol());
						newCurrencies.add(ticker);
						tickersDB.add(ticker);
					}
				}
			}
		} else {
			log.warn("Api /api/v1/ticker/allBookTickers returned null list!");
			return tickersDB;
		}

		log.debug("Currencies to save: " + tickersDB.toString());


		tickerRepository.save(tickersDB);
		//TODO handle some notification

		return tickersDB;
	}

	@Override
	public List<Candlestick> getCandlestickBars(String symbol, String interval, Integer limit, Long startTime, Long endTime) {
		return client.getCandlestickBars(symbol, MapperUtil.stringToCandlestickInterval(interval), limit, startTime, endTime);
	}

	@Override
	public List<Candlestick> getCandlestickBars(String symbol, String interval) {
		return client.getCandlestickBars(symbol, MapperUtil.stringToCandlestickInterval(interval));
	}

	@Override
	public List<Candlestick> getCandlestickBars(String symbol, String interval, Integer limit) {
		return client.getCandlestickBars(symbol, MapperUtil.stringToCandlestickInterval(interval), limit, null, null);
	}

	@Override
	public TickerPrice getLastPrice(String symbol) {
		return client.getPrice(symbol);
	}

	@Override
	public List<TickerPrice> getLastPrices() {
		return client.getAllPrices();
	}

	@Override
	public NewOrderResponse newOrder(NewOrder order) {
		return client.newOrder(order);
	}

	@Override
	public void newOrderTest(NewOrder order) {
		client.newOrderTest(order);
	}

	@Override
	public Order getOrderStatus(OrderStatusRequest orderStatusRequest) {
		return client.getOrderStatus(orderStatusRequest);
	}

	@Override
	public void cancelOrder(CancelOrderRequest cancelOrderRequest) {
		client.cancelOrder(cancelOrderRequest);
	}

	@Override
	public List<Order> getOpenOrders(OrderRequest orderRequest) {
		return client.getOpenOrders(orderRequest);
	}

	@Override
	public TickerStatistics get24HrPriceStatistics(String symbol) {
		return client.get24HrPriceStatistics(symbol);
	}

	@Override
	public Account getAccount(Long recvWindow, Long timestamp) {
		return client.getAccount(recvWindow, timestamp);
	}


}
