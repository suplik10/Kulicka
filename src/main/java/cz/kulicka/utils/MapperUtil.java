package cz.kulicka.utils;

import cz.kulicka.entity.Kline;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import java.util.ArrayList;

@Deprecated
public class MapperUtil {

    static Logger log = Logger.getLogger(MapperUtil.class);

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
}
