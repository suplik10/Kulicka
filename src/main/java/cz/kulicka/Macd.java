package cz.kulicka;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.kulicka.entity.Kline;
import cz.kulicka.utils.MapperUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class Macd {

//    https://github.com/sergiocormio/stock-alerts/
//    http://www.iexplain.org/ema-how-to-calculate/.
//    https://www.investujeme.cz/clanky/macd-temer-svaty-gral/


    public static void main(String[] args) {

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

        Collections.reverse(klines);




//        MACD = EMA (12) – EMA (26)
//
//        Pro jasné znázornění nákupních a prodejních příkazů se používá ještě třetí exponenciální klouzavý průměr s délkou 9 období, který tvoří tzv. signální křivku.
//
//        Signal = EMA (9)
//
//        Samotný indikátor výrazně zdokonalil Thomas Aspray v roce 1986, kdy znázornil indikátor MACD ve formě histogramu. Výpočet je jednoduchý
//
//        Histogram = MACD – signal
//http://www.dummies.com/personal-finance/investing/stocks-trading/how-to-track-trading-momentum-with-macd/

        ArrayList<Float> emaShort = new ArrayList<>();

        ArrayList<Float> emaLong = new ArrayList<>();

        Macd v = new Macd();
        v.emaCalc(emaShort, klines, 3);

        v.emaCalc(emaLong, klines, 12);

        ArrayList<Float> macdik = new ArrayList<>();

        for (int i = 0; emaLong.size() > i; i++){
            macdik.add(emaLong.get(i) - emaShort.get(i));


            System.out.println("macd: - " + i + " - " + new Date(klines.get(i).getCloseTime()) + " " + (emaLong.get(i) - emaShort.get(i)));
        }

    }

    public float CalculateEMA(float closingPrice, float numberOfDays, float EMAYesterday){

        // (2/(selected time period + 1) ) = (2/(10 + 1) ) = 0.1818 (18.18%)
        float multiplier = 2 / (numberOfDays + 1);

        return (closingPrice-EMAYesterday) * multiplier + EMAYesterday;

        //return todaysPrice * k + EMAYesterday * (1 - k);
    }

    public void emaCalc(ArrayList<Float> emaList, ArrayList<Kline> klines, float days){

        float ema;
        float yesterdayEMA = 0;

        for (Kline kline: klines){
            //call the EMA calculation
            ema = CalculateEMA((float)kline.getClose(), days, yesterdayEMA);
            //put the calculated ema in an array
            emaList.add(ema);
            //make sure yesterdayEMA gets filled with the EMA we used this time around
            yesterdayEMA = ema;


        }
    }


//    public float CalculateEMA(float todaysPrice, float numberOfDays, float EMAYesterday){
//        float k = 2 / (numberOfDays + 1);
//        return todaysPrice * k + EMAYesterday * (1 - k);
//    }
//
//    public void emaCalc(ArrayList<Float> emaList, ArrayList<Kline> klines, float days){
//
//        float ema;
//        float yesterdayEMA = 0;
//
//        for (Kline kline: klines){
//            //call the EMA calculation
//            ema = CalculateEMA((float)kline.getClose(), days, yesterdayEMA);
//            //put the calculated ema in an array
//            emaList.add(ema);
//            //make sure yesterdayEMA gets filled with the EMA we used this time around
//            yesterdayEMA = ema;
//
//
//        }
//    }
}
