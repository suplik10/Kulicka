package cz.kulicka.utils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.kulicka.entity.Kline;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MapperUtil {

    static Logger log = Logger.getLogger(MapperUtil.class);

    @Deprecated
    public static ArrayList<Kline> klinesJsonArrayToKlinesObjectArray(ArrayList<ArrayList<String>> klinesJson) {
        log.info("Map klines json array to klines object Array - START ");

        Assert.notNull(klinesJson, "Klines json cannot be null!");

        ArrayList<Kline> klines = new ArrayList<Kline>();

        for (int i = 0; i < klinesJson.size(); i++) {
            klines.add(new Kline(klinesJson.get(i)));
        }

        log.info("Map klines json array to klines object Array - DONE! ");

        return klines;
    }

    public static String listOfFloatToJson(List<Float> list) {
        String jsonList = null;

        final ObjectMapper mapper = new ObjectMapper();
        try {
            jsonList = mapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            log.error("Mapping list of float to json exception: " + e.getMessage());
        }

        return jsonList;
    }

    public static ArrayList<Float> jsonToListOfFloat(String json) {
        ArrayList<Float> list = new ArrayList<>();

        final ObjectMapper objectMapper = new ObjectMapper();

        ArrayList<String> result = null;
        try {
            list = objectMapper.readValue(json, new TypeReference<ArrayList<Float>>() {
            });
        } catch (JsonMappingException e) {
            log.error("Mapping json to list of float exception: " + e.getMessage());
        } catch (JsonParseException e) {
            log.error("Mapping json to list of float exception: " + e.getMessage());
        } catch (IOException e) {
            log.error("Mapping json to list of float exception: " + e.getMessage());
        }
        return list;
    }
}
