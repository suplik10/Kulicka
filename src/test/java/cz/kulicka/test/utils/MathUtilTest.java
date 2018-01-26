package cz.kulicka.test.utils;

import cz.kulicka.utils.MathUtil;
import org.junit.Assert;
import org.junit.Test;

public class MathUtilTest {

    @Test
    public void getPercengate() {
        //MathUtil.getPercentageProfit(null, 5);
        Assert.assertEquals(50, MathUtil.getPercentageProfit(80,120), 0);
        Assert.assertEquals(-61.25, MathUtil.getPercentageProfit(80,31), 0);
        //Assert.assertEquals(-61.25, MathUtil.getPercentageProfit(0.00001225,0.00001194), 0);

    }
}
