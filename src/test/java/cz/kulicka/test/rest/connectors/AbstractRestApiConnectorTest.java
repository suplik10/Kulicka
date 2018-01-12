package cz.kulicka.test.rest.connectors;

import cz.kulicka.rest.connectors.AbstractRestApiConnector;
import org.junit.Test;

import java.io.IOException;

public class AbstractRestApiConnectorTest {
    @Test
    public void testRestUrl() throws IOException {

        // https://www.binance.com/api/v1/klines?symbol=BTCUSDT&interval=1d&limit=5


        // Assert.assertNotNull(result);

        String v = AbstractRestApiConnector.executeHttpGetRequest("sdf");
    }
}
