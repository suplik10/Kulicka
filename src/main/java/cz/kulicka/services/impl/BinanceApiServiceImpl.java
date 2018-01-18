package cz.kulicka.services.impl;

import cz.kulicka.BinanceApiClientFactory;
import cz.kulicka.constant.CurrenciesConstants;
import cz.kulicka.entities.BookTicker;
import cz.kulicka.entities.Candlestick;
import cz.kulicka.entities.NewOrder;
import cz.kulicka.entities.NewOrderResponse;
import cz.kulicka.entities.Order;
import cz.kulicka.entities.OrderRequest;
import cz.kulicka.entities.Ticker;
import cz.kulicka.entities.TickerPrice;
import cz.kulicka.entities.request.CancelOrderRequest;
import cz.kulicka.entities.request.OrderStatusRequest;
import cz.kulicka.enums.CandlestickInterval;
import cz.kulicka.repository.TickerRepository;
import cz.kulicka.rest.connectors.BinanceApiRestClient;
import cz.kulicka.services.BinanceApiService;
import cz.kulicka.utils.CommonUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BinanceApiServiceImpl implements BinanceApiService {

    static Logger log = Logger.getLogger(BinanceApiServiceImpl.class);

    @Autowired
    TickerRepository tickerRepository;

    //TODO refactor to spring
    BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
    BinanceApiRestClient client = factory.newRestClient();

    @Override
    public ArrayList<Ticker> checkActualCurrencies(ArrayList newCurrencies) {

        List<BookTicker> newBookTickers = client.getBookTickers();

        ArrayList<Ticker> tickersDB = (ArrayList<Ticker>) tickerRepository.findAll();

        if (newBookTickers != null) {
            tickerRepository.deleteAll();
            for (int i = 0; i < newBookTickers.size(); i++) {
                if (newBookTickers.get(i).getSymbol().contains(CurrenciesConstants.BTC)) {
                    if(CommonUtil.addTickerToDBList(tickersDB, newBookTickers.get(i).getSymbol())){
                        Ticker ticker = new Ticker(newBookTickers.get(i).getSymbol());
                        newCurrencies.add(ticker);
                        tickersDB.add(ticker);
                    }
                }
            }
        }else{
            log.warn("Api /api/v1/ticker/allBookTickers returned null list!");
            return tickersDB;
        }

        log.info("Currencies to save: " + tickersDB.toString());

        tickerRepository.save(tickersDB);

        log.info("New currencies on exchange! : " + newCurrencies.toString());

        return tickersDB;
    }

    @Override
    public List<Candlestick> getCandlestickBars(String symbol, CandlestickInterval interval, Integer limit, Long startTime, Long endTime) {
        return client.getCandlestickBars(symbol, interval, limit, startTime, endTime);
    }

    @Override
    public List<Candlestick> getCandlestickBars(String symbol, CandlestickInterval interval) {
        return client.getCandlestickBars(symbol, interval);
    }

    @Override
    public List<Candlestick> getCandlestickBars(String symbol, CandlestickInterval interval, Integer limit) {
        return client.getCandlestickBars(symbol, interval, limit);
    }

    @Override
    public TickerPrice getLastPrice(String symbol) {
        return client.getLastPrice(symbol);
    }

    @Override
    public List<TickerPrice> getLastPrices() {
        return client.getLastPrices();
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


}
