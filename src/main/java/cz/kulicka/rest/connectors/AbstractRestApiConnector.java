package cz.kulicka.rest.connectors;

import cz.kulicka.services.impl.BinanceApiServiceImpl;
import org.apache.log4j.Logger;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public final class AbstractRestApiConnector {

    static Logger log = Logger.getLogger(AbstractRestApiConnector.class);

    public static String executeHttpGetRequest(String url2) {

        try {



            URL url = new URL("https://api.binance.com/api/v1/ticker/allPrices");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }

            conn.disconnect();

        } catch (
                MalformedURLException e)

        {

            e.printStackTrace();

        } catch (
                IOException e)

        {

            e.printStackTrace();

        }

        return "sdf";

    }


}
