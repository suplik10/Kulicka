package cz.kulicka.test;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.kulicka.entity.Market;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;


public class MarketTest {

    @Test
    public void allPriceJsonMapper() throws IOException {

        File jsonFile = new File("src/test/resources/markets.json");

        ObjectMapper objectMapper = new ObjectMapper();

        Market result = null;
        try {
            result = objectMapper.readValue(jsonFile, Market.class);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        }

        Assert.assertNotNull(result);

    }
}
