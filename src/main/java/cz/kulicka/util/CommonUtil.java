package cz.kulicka.util;

import cz.kulicka.entity.Ticker;
import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class CommonUtil {

    static Logger log = Logger.getLogger(CommonUtil.class);

    public static boolean addTickerToDBList(ArrayList<Ticker> DBList, String newSymbol, List<String> blackList, boolean ignoreBlackList) {
        Validate.notNull(newSymbol);
        Validate.notNull(blackList);

        boolean foundAtBlackList = false;

        if (DBList == null) {
            DBList = new ArrayList<>();
        }

        //&& !newBookTickers.get(i).getSymbol().contains(CurrenciesConstants.BNB) && !newBookTickers.get(i).getSymbol().contains(CurrenciesConstants.USDT)

        if (!ignoreBlackList) {
            for (String blackListSymbol : blackList) {
                if (newSymbol.equals(blackListSymbol)) {
                    foundAtBlackList = true;
                    break;
                }
            }

            if (foundAtBlackList) {
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
                return "CANDLESTICK_PERIOD_CROSS_DOWN_EMA";
            case 7:
                return "TRAILING_STOP_CROSS_DOWN_EMA";
            case 8:
                return "INSTA_SELL_CROSS_DOWN_EMA";
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
