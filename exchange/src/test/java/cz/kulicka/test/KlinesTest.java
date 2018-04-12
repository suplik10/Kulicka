package cz.kulicka.test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class KlinesTest {

    @Test
    public void klinesJsonMapper() throws IOException {

        File jsonFile = new File("src/test/resources/klines.json");

        ObjectMapper objectMapper = new ObjectMapper();

        ArrayList<String> result = null;
        try {
            result = objectMapper.readValue(jsonFile, new TypeReference<ArrayList<ArrayList<String>>>() {
            });
        } catch (JsonMappingException e) {
            e.printStackTrace();
        }

        Assert.assertNotNull(result);

    }
}