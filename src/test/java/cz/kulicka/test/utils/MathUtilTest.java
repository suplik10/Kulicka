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

        int takeProfitPercentage = 5;

        int stoplossPercentage = -2;

        int stoplossfirst = 2;

        double result = MathUtil.getPercentageDifference(0.0006655, 0.0005668);

        int level = 1;

        double upPercentage = 1;

        double downPercentage = 1;

        double up = (takeProfitPercentage + (level * upPercentage));

        double down = ((takeProfitPercentage - 2) + (level * downPercentage));

        if (result > (takeProfitPercentage + (level * upPercentage))) {
            //TODO posunuju up level
        } else if (result < (stoplossPercentage + (level * downPercentage))) {
            //TODO prodavam
            String bla = "fsdf";
        }
        //todo nic

        double reslut3 = MathUtil.getValuePercentage(100, 20);

        double reslut4 = MathUtil.getValuePercentage(10, 20);

        double reslut5 = MathUtil.getValuePercentage(0.5, 10);

        boolean hm = 0.8 > (0.5 + MathUtil.getValuePercentage(0.5, 10));

        double dif = MathUtil.getPercentageDifference(0.00000883379, 0.00000889828);


        double lastPrice = 0.108076;
        double buyBTC = 0.002;

        double res = buyBTC / lastPrice;
        String resString = String.valueOf(res);
        String fasfd = resString.substring(resString.indexOf("."), resString.indexOf(".") + 3);

        String resultString = String.valueOf(round(res, 0));

        double result2 = MathUtil.getPercentageDifference(100, 101);


    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    @Test
    public void isCrossDownEma() {

        boolean result;

        double lastlong =  0.000149797;
        double lastshort = 0.000149927;
        double propertyIntolerantion = -0.1;

        //test sell
        result = (MathUtil.getPercentageDifference(lastlong, lastshort) + propertyIntolerantion) < 0;



        //test buy
        //lastlong =  0.000084276;
        //lastshort = 0.000084335;
        double propertyIntolerantionBuy = 0.0;

        result = MathUtil.getPercentageDifference(lastlong, lastshort) - propertyIntolerantionBuy > 0;

        boolean v = result;



    }

}
