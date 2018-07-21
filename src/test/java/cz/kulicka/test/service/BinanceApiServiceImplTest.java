package cz.kulicka.test.service;

import com.binance.api.client.domain.market.Candlestick;
import cz.kulicka.service.BinanceApiServiceMKA;
import cz.kulicka.service.impl.BinanceApiServiceMKAImpl;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;


public class BinanceApiServiceImplTest {

	BinanceApiServiceMKA binanceApiServiceMKA = new BinanceApiServiceMKAImpl();

	//@Test
	public void checkActualCurrenciesTest() {

		ArrayList<String> as = new ArrayList<>();
		binanceApiServiceMKA.checkActualCurrencies(as);

		Assert.assertEquals(0, as.size());

	}

	//@Test
	public void getCandlestickBarsTest() {

		List<Candlestick> as = binanceApiServiceMKA.getCandlestickBars("ETHBTC", "1d", 10);

		Assert.assertNotNull(as);
		Assert.assertNotEquals(0, as.size());
		Assert.assertEquals(10, as.size());

	}
}
