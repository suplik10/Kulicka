package cz.kulicka.test.rest.connectors;

import cz.kulicka.entity.BookTicker;
import cz.kulicka.rest.client.BinanceApiRestClient;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class AbstractRestApiConnectorTest {
    @Test
    public void testRestUrl() throws IOException {

        // https://www.binance.com/api/v1/klines?symbol=BTCUSDT&interval=1d&limit=5
//        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
//        BinanceApiRestClient client = factory.newRestClient();
//
//        List<BookTicker> a = client.getBookTickers();

        // Assert.assertNotNull(result);

       // String v = AbstractRestApiConnector.executeHttpGetRequest("sdf");
    }
}
