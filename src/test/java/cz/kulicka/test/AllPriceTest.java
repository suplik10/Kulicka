package cz.kulicka.test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.kulicka.entities.AllPrice;
import org.junit.Assert;
import org.junit.Test;


import java.io.File;
import java.io.IOException;
import java.util.List;

public class AllPriceTest {

    @Test
    public void allPriceJsonMapper() throws IOException {

        File jsonFile = new File("src/test/resources/allPrices.json");

        ObjectMapper objectMapper = new ObjectMapper();

        List<AllPrice> result = null;
        try {
            result = objectMapper.readValue(jsonFile, new TypeReference<List<AllPrice>>(){});
        } catch (JsonMappingException e) {
            e.printStackTrace();
        }

        Assert.assertNotNull(result);

    }
}
