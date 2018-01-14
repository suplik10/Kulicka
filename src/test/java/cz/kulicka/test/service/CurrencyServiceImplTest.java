package cz.kulicka.test.service;

import cz.kulicka.entities.Candlestick;
import cz.kulicka.entities.CandlestickInterval;
import cz.kulicka.services.CurrencyService;
import cz.kulicka.services.impl.CurrencyServiceImpl;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


public class CurrencyServiceImplTest {

    CurrencyService currencyService = new CurrencyServiceImpl();

    @Test
    public void checkActualCurrenciesTest() {

        ArrayList<String> as = new ArrayList<>();
        currencyService.checkActualCurrencies(as);

        Assert.assertEquals(0, as.size());

    }

    @Test
    public void getCandlestickBarsTest() {

        List<Candlestick> as = currencyService.getCandlestickBars("ETHBTC", CandlestickInterval.DAILY, 10);

        Assert.assertNotNull(as);
        Assert.assertNotEquals(0, as.size());
        Assert.assertEquals(10, as.size());

    }
}
