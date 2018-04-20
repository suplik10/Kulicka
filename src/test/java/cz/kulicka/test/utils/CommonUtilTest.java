package cz.kulicka.test.utils;


import cz.kulicka.CoreEngine;
import cz.kulicka.util.CommonUtil;
import cz.kulicka.util.MathUtil;
import org.junit.Assert;
import org.junit.Test;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class CommonUtilTest {

    @Test
    public void getBuySellReasonTest() {

        Assert.assertEquals("CANDLESTICK_PERIOD_STOPLOSS", CommonUtil.convertSellReasonToString(1));
        Assert.assertEquals("MACD_BUY", CommonUtil.convertBuyReasonToString(0));

        String pair = "FWWWWWBTC";
        String symbol =  pair.substring(0,pair.length()-3); //pair.replace("BTC", "");
        String v = "s";

        String pair11 = "1.000000";

        int noOfDecimals = pair11.substring(pair11.indexOf("."), pair11.indexOf("1")).length();

        String quantity = Double.toString(MathUtil.cutDecimalsWithoutRound(1.9019, 0));



        String noOfDecimalsStr = pair11.substring(2, pair11.indexOf("1"));

        double d = 0.436789436287643872;
        double r = MathUtil.cutDecimalsWithoutRound(d,5);



    }

}
