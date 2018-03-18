package cz.kulicka.test.utils;

import cz.kulicka.util.MathUtil;
import org.junit.Assert;
import org.junit.Test;

public class MathUtilTest {

    @Test
    public void getPercengate() {
        //MathUtil.getPercentageProfit(null, 5);
        Assert.assertEquals(50, MathUtil.getPercentageDifference(80, 120), 0);
        Assert.assertEquals(-61.25, MathUtil.getPercentageDifference(80, 31), 0);

        int buyPrice = 100;

        int takeProfitPercentage = 1;

        int stoplossPercentage = -2;

        double result = MathUtil.getPercentageDifference(100, 120);

        int level = 2;

        double upPercentage = 1;

        double downPercentage = 1;

        if (result > (takeProfitPercentage + (level * upPercentage))) {
            //TODO posunuju up level
        } else if (result <  (stoplossPercentage + (level * downPercentage))) {
            //TODO prodavam
            String bla = "fsdf";
        }
        //todo nic


        double lastPrice = 0.0011388;
        double sellPrice = 0.0011355;

        double result2 = MathUtil.getPercentageDifference(sellPrice, lastPrice);

    }
}
