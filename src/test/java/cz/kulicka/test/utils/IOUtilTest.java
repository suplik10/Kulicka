package cz.kulicka.test.utils;

import cz.kulicka.entity.Order;
import cz.kulicka.util.IOUtil;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

import static cz.kulicka.util.IOUtil.loadListOfStringsToFile;
import static cz.kulicka.util.IOUtil.saveListOfStringsToFile;

public class IOUtilTest {

    @Test
    public void saveFile() throws IOException {
        ArrayList<String> testArray = new ArrayList<>();
        testArray.add("jedna");
        testArray.add("dva");
        testArray.add("tri");

        Assert.assertTrue(saveListOfStringsToFile(testArray, "src/test/resources/IOTestFile"));
    }


    @Test
    public void loadFile() throws IOException {

        Assert.assertEquals(loadListOfStringsToFile("src/test/resources/IOTestFile").size(), 3);

    }

    @Test
    public void saveToCSV() throws IOException {

        Order order = new Order();

        order.setBuyTime(1519992000000l);
        order.setSellTime(1519992299999l);
        order.setSymbol("BNBBTC");
        order.setBuyPriceForOrderWithFee(0.00030080);
        order.setSellPriceForOrderWithFee(0.00029790);
        order.setProfitFeeIncluded(3.3333);
        order.setSellReason(2);

        ArrayList<Order> orders = new ArrayList<>();
        orders.add(order);

        Assert.assertEquals(true, IOUtil.finishedOrderToCsv(orders, "C:/APPS/JavaDev/reportTest.txt", false, new ArrayList<>()));

    }


}
