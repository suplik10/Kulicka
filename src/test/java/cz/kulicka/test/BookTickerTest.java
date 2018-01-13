package cz.kulicka.test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.kulicka.entities.BookTicker;
import org.junit.Assert;
import org.junit.Test;


import java.io.File;
import java.io.IOException;
import java.util.List;

public class BookTickerTest {

    @Test
    public void allPriceJsonMapper() throws IOException {

        File jsonFile = new File("src/test/resources/allBookTickers.json");

        ObjectMapper objectMapper = new ObjectMapper();

        List<BookTicker> result = null;
        try {
            result = objectMapper.readValue(jsonFile, new TypeReference<List<BookTicker>>(){});
        } catch (JsonMappingException e) {
            e.printStackTrace();
        }

        Assert.assertNotNull(result);

    }
}
