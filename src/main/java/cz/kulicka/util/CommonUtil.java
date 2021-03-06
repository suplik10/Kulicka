package cz.kulicka.util;

import com.binance.api.client.domain.general.FilterType;
import com.binance.api.client.domain.general.SymbolInfo;
import com.binance.api.client.exception.BinanceApiException;
import cz.kulicka.entity.Ticker;
import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class CommonUtil {

	static Logger log = Logger.getLogger(CommonUtil.class);

	public static boolean addTickerToDBList(ArrayList<Ticker> DBList, String newSymbol, List<String> blacklist, boolean ignoreBlacklist, List<String> whitelist, boolean ignoreWhitelist) {
		Validate.notNull(newSymbol);

		boolean foundAtBlackList = false;

		if (DBList == null) {
			DBList = new ArrayList<>();
		}


		if (!ignoreBlacklist) {
			for (String blackListSymbol : blacklist) {
				if (newSymbol.equals(blackListSymbol)) {
					foundAtBlackList = true;
					break;
				}
			}

			if (foundAtBlackList) {
				return false;
			} else {
				return isNotInDb(DBList, newSymbol);
			}
		} else if (!ignoreWhitelist) {
			for (String whitelistSymbol : whitelist) {
				if (newSymbol.equals(whitelistSymbol)) {
					return isNotInDb(DBList, newSymbol);
				}
			}
			return false;
		}
		return isNotInDb(DBList, newSymbol);
	}

	private static boolean isNotInDb(ArrayList<Ticker> DBList, String newSymbol) {

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

	public static int getNumberOfDecimalPlacesToOrder(String symbolPair, List<SymbolInfo> symbols) {

		SymbolInfo symbolInfo = symbols.stream().filter(symbolInfo1 -> symbolInfo1.getSymbol().equals(symbolPair))
				.findFirst()
				.orElseThrow(() -> new BinanceApiException("Unable to obtain information for symbol " + symbolPair));

		String stepSize = symbolInfo.getSymbolFilter(FilterType.LOT_SIZE).getStepSize();

		try {
			try {
				return stepSize.substring(stepSize.indexOf("."), stepSize.indexOf("1")).length();
			} catch (StringIndexOutOfBoundsException e) {
				return 0;
			}
		} catch (RuntimeException e) {
			log.error("Failed to parse coin decimal places > coin symbol " + symbolPair + " EXCEPTION: " + e.getStackTrace());
			throw new BinanceApiException("Failed to parse coin decimal places > coin symbol " + symbolPair);
		}
	}
}
