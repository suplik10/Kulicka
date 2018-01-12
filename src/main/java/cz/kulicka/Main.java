package cz.kulicka;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.kulicka.rest.connectors.AbstractRestApiConnector;
import cz.kulicka.utils.Mapper;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;


public class Main {

    static Logger log = Logger.getLogger(Main.class);

    public static void main(String[] args) throws IOException {


//        try {
//
//            URL url = new URL("http://www.worldcoinindex.com/apiservice/json?key=gqbd0KQ7dBQHkfQacCunQF3zA3AHhO");
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("GET");
//            conn.setRequestProperty("Accept", "application/json");
//
//            if (conn.getResponseCode() != 200) {
//                throw new RuntimeException("Failed : HTTP error code : "
//                        + conn.getResponseCode());
//            }
//
//            BufferedReader br = new BufferedReader(new InputStreamReader(
//                    (conn.getInputStream())));
//
//            String output;
//            System.out.println("Output from Server .... \n");
//            while ((output = br.readLine()) != null) {
//                System.out.println(output);
//            }
//
//            conn.disconnect();
//
//        } catch (MalformedURLException e) {
//
//            e.printStackTrace();
//
//        } catch (IOException e) {
//
//            e.printStackTrace();
//
//        }
    }

}
