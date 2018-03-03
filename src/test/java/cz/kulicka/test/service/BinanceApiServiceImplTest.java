package cz.kulicka.test.service;

import cz.kulicka.entity.Candlestick;
import cz.kulicka.services.BinanceApiService;
import cz.kulicka.services.impl.BinanceApiServiceImpl;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;


public class BinanceApiServiceImplTest {

    BinanceApiService binanceApiService = new BinanceApiServiceImpl();

    //@Test
    public void checkActualCurrenciesTest() {

        ArrayList<String> as = new ArrayList<>();
        binanceApiService.checkActualCurrencies(as);

        Assert.assertEquals(0, as.size());

    }

    //@Test
    public void getCandlestickBarsTest() {

        List<Candlestick> as = binanceApiService.getCandlestickBars("ETHBTC", "1d", 10);

        Assert.assertNotNull(as);
        Assert.assertNotEquals(0, as.size());
        Assert.assertEquals(10, as.size());

    }
}
