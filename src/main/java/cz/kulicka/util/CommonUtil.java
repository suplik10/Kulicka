package cz.kulicka.util;

import cz.kulicka.entity.Ticker;
import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class CommonUtil {

    static Logger log = Logger.getLogger(CommonUtil.class);

    public static boolean addTickerToDBList(ArrayList<Ticker> DBList, String newSymbol, List<String> whiteList, boolean ignoreWhitelist) {
        Validate.notNull(newSymbol);
        Validate.notNull(whiteList);

        boolean foundAtWhitelist = false;

        if (DBList == null) {
            DBList = new ArrayList<>();
        }

        if (!ignoreWhitelist) {
            for (String whiteListSymbol : whiteList) {
                if (newSymbol.contains(whiteListSymbol)) {
                    foundAtWhitelist = true;
                    break;
                }
            }

            if (!foundAtWhitelist) {
                return false;
            }
        }

        log.trace("FoundAtWhitelist symbol: " + newSymbol);

        for (Ticker ticker : DBList) {
            if (ticker.getSymbol().equals(newSymbol))
                return false;
        }

        log.trace("Save to db symbol: " + newSymbol);
        return true;
    }
}
