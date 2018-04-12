package cz.kulicka.test.utils;


import cz.kulicka.util.CommonUtil;
import org.junit.Assert;
import org.junit.Test;

public class CommonUtilTest {

    @Test
    public void getBuySellReasonTest() {

        Assert.assertEquals("CANDLESTICK_PERIOD_STOPLOSS", CommonUtil.convertSellReasonToString(1));
        Assert.assertEquals("MACD_BUY", CommonUtil.convertBuyReasonToString(0));
    }
}
