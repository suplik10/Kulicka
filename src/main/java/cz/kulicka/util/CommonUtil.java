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
                if (newSymbol.equals(whiteListSymbol)) {
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

    public static String convertSellReasonToString(int sellReason) {

        switch (sellReason) {
            case 0:
                return "CANDLESTICK_PERIOD_TAKE_PROFIT";
            case 1:
                return "CANDLESTICK_PERIOD_STOPLOSS";
            case 2:
                return "CANDLESTICK_PERIOD_NEGATIVE_MACD";
            case 3:
                return "INSTA_SELL_TAKE_PROFIT";
            case 4:
                return "INSTA_SELL_STOPLOSS";
            case 5:
                return "INSTA_SELL_TRAILING_STOP_STOPLOSS";
            case 6:
                return "CANDLESTICK_PERIOD_UNCONFIRMED_EMA";
            default:
                throw new IllegalArgumentException("Invalid sellReason!");
        }
    }

    public static String convertBuyReasonToString(int buyReason) {

        switch (buyReason) {
            case 0:
                return "MACD_BUY";
            case 1:
                return "STOPLOSS_PROTECTION_REBUY";
            case 2:
                return "EMA_BUY";
            default:
                throw new IllegalArgumentException("Invalid buyReason!");
        }
    }
}
