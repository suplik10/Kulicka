package cz.kulicka;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.kulicka.entity.ChartKline;
import cz.kulicka.entity.Kline;
import cz.kulicka.entity.TradingData;
import cz.kulicka.utils.MapperUtil;
import cz.kulicka.utils.MathUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class Macd {

//    https://github.com/sergiocormio/stock-alerts/
//    http://www.iexplain.org/ema-how-to-calculate/.
//    https://www.investujeme.cz/clanky/macd-temer-svaty-gral/
    //https://www.binance.com/api/v1/klines?symbol=BNBUSDT&interval=1h&limit=500
    //chtěl bych tam vidět sloupce: čas, coin, buy price, sell price, důvod sell, profit


    public static void main(String[] args) {


    }

    public ArrayList<ChartKline> getDataSet() {
        File jsonFile = new File("src/test/resources/klines.json");

        ObjectMapper objectMapper = new ObjectMapper();

        ArrayList<ArrayList<String>> result = null;
        try {
            result = objectMapper.readValue(jsonFile, new TypeReference<ArrayList<ArrayList<String>>>() {
            });
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<Kline> klines = MapperUtil.klinesJsonArrayToKlinesObjectArray(result);

        ArrayList<Float> lastKlinesPrices = new ArrayList<>();

        for (Kline kline: klines){
            lastKlinesPrices.add((float) kline.getClose());
        }

        TradingData tradingData = MathUtil.getTradingData(lastKlinesPrices, 12,26,9, 0, 0 ,0 );

        ArrayList<ChartKline> chartKlines = new ArrayList<>();

        for (int i = 0; tradingData.getEmaLong().size() > i; i++) {
            ChartKline chartKline = new ChartKline();
            chartKline.setValue(tradingData.getMACDHistogram().get(i));
            chartKline.setClosedDate(new Date(klines.get(i).getOpenTime()));
            chartKline.setClosedPrice(klines.get(i).getClose());
            chartKlines.add(chartKline);
        }

        chartKlines.subList(0, 100).clear();

        return chartKlines;
    }
}
