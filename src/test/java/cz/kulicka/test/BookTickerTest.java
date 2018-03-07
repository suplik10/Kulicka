package cz.kulicka.test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.kulicka.constant.CurrenciesConstants;
import cz.kulicka.entity.BookTicker;
import cz.kulicka.entity.Ticker;
import cz.kulicka.service.impl.BinanceApiServiceImpl;
import cz.kulicka.util.CommonUtil;
import org.junit.Assert;
import org.junit.Test;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BookTickerTest {

    @Test
    public void allPriceJsonMapper() throws IOException {

        File jsonFile = new File("src/test/resources/allBookTickers.json");

        ObjectMapper objectMapper = new ObjectMapper();

        List<BookTicker> result = null;
        ArrayList<Ticker> newCur = new ArrayList<>();
        ArrayList<Ticker> tickersDB = new ArrayList<>();
        try {
            result = objectMapper.readValue(jsonFile, new TypeReference<List<BookTicker>>(){});




            for (int i = 0; i < result.size(); i++) {
                if (result.get(i).getSymbol().contains(CurrenciesConstants.BTC)) {
                    if (CommonUtil.addTickerToDBList(tickersDB, result.get(i).getSymbol(), null)) {
                        Ticker ticker = new Ticker(result.get(i).getSymbol());
                        newCur.add(ticker);
                        tickersDB.add(ticker);
                    }
                }
            }
        } catch (JsonMappingException e) {
            e.printStackTrace();
        }



        Assert.assertNotNull(result);

    }
}
