package cz.kulicka.utils;

import cz.kulicka.entities.Kline;
import org.apache.log4j.Logger;
import org.junit.Assert;

import java.util.ArrayList;

public final class Mapper {

    static Logger log = Logger.getLogger(Mapper.class);

    public static ArrayList<Kline> klinesJsonArrayToKlinesObjectArray(ArrayList<ArrayList<String>> klinesJson) {
        log.info("Map klines json array to klines object Array - START ");

        Assert.assertNotNull(klinesJson);

        ArrayList<Kline> klines = new ArrayList<Kline>();

        for (int i = 0; i < klinesJson.size(); i++) {
            klines.add(new Kline(klinesJson.get(i)));
        }

        log.info("Map klines json array to klines object Array - DONE! ");

        return klines;
    }
}
