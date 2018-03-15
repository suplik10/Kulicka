package cz.kulicka.util;

import cz.kulicka.entity.TradingData;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;

public class MathUtil {

    public static double getPercentageProfit(double buyPrice, double actualPrice) {
        return (actualPrice / (buyPrice / 100)) - 100;
    }

    public static double getSellPriceForOrderWithFee(double boughtAmount, double tickerPricesForUnitUSDT, double sellFeeConstant) {
        return (boughtAmount * tickerPricesForUnitUSDT) - ((boughtAmount * tickerPricesForUnitUSDT) * (sellFeeConstant / 100));
    }

    //    https://github.com/sergiocormio/stock-alerts/
    //    http://www.iexplain.org/ema-how-to-calculate/.
    //    https://www.investujeme.cz/clanky/macd-temer-svaty-gral/
    //    https://www.binance.com/api/v1/klines?symbol=BNBUSDT&interval=1h&limit=500

    public static TradingData getTradingData(String symbol, Long orderId, ArrayList<Float> candlestickData, float emaShortDays, float emaLongDays,
                                             float signalDays, float emaShortYesterday, float emaLongYesterday, float emaSignalYesterday) {

        TradingData tradingData = getEmaShortLongTradingData(symbol, orderId, candlestickData, emaShortDays, emaLongDays, emaShortYesterday, emaLongYesterday);

        tradingData.setMACDLine(new ArrayList<>());

        for (int i = 0; tradingData.getEmaShort().size() > i; i++) {
            tradingData.getMACDLine().add(tradingData.getEmaShort().get(i) - tradingData.getEmaLong().get(i));
        }

        tradingData.setEmaSignal(getEmaFrom(tradingData.getMACDLine(), signalDays, emaSignalYesterday));

        tradingData.setMACDHistogram(new ArrayList<>());

        for (int i = 0; tradingData.getEmaSignal().size() > i; i++) {
            tradingData.getMACDHistogram().add(tradingData.getMACDLine().get(i) - tradingData.getEmaSignal().get(i));
        }

        tradingData.updateFields();

        return tradingData;

    }


    // https://www.tradingview.com/wiki/Moving_Average
    private static float calculateEMA(float closingPrice, float numberOfDays, float EMAYesterday) {
        float multiplier = 2 / (numberOfDays + 1);
        return (closingPrice - EMAYesterday) * multiplier + EMAYesterday;
    }

    private static ArrayList<Float> getEmaFrom(ArrayList<Float> data, float days, float yesterdayEMA) {

        ArrayList<Float> emaList = new ArrayList<>();

        float ema;

        for (Float price : data) {
            //call the EMA calculation
            ema = calculateEMA(price, days, yesterdayEMA);
            //put the calculated ema in an array
            emaList.add(ema);
            //make sure yesterdayEMA gets filled with the EMA we used this time around
            yesterdayEMA = ema;
        }

        return emaList;
    }

    public static TradingData getEmaShortLongTradingData(String symbol, Long orderId, ArrayList<Float> candlestickData,
                                                         float emaShortDays, float emaLongDays, float emaShortYesterday, float emaLongYesterday) {
        Validate.notEmpty(candlestickData);

        TradingData tradingData = new TradingData(symbol, orderId);

        tradingData.setEmaShort(getEmaFrom(candlestickData, emaShortDays, emaShortYesterday));
        tradingData.setEmaLong(getEmaFrom(candlestickData, emaLongDays, emaLongYesterday));

        tradingData.updateFields();

        return tradingData;

    }

}
