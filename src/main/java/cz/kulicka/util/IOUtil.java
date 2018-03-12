package cz.kulicka.util;

import cz.kulicka.entity.Order;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class IOUtil {

    static Logger log = Logger.getLogger(IOUtil.class);

    @Deprecated
    public static boolean saveListOfStringsToFile(ArrayList<String> listToSave, String filePath) {
        FileOutputStream fileOutputStream = null;
        ObjectOutputStream objectOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(filePath);
        } catch (FileNotFoundException e) {
            log.error("File not found exception: " + e.getStackTrace());
            return false;
        }

        try {
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(listToSave);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            log.error("IO exception: " + e.getStackTrace());
            return false;
        }
        return true;
    }

    @Deprecated
    public static ArrayList<String> loadListOfStringsToFile(String filePath) {
        FileInputStream fileInputStream = null;
        ObjectInputStream objectInputStream = null;
        ArrayList<String> result = null;

        try {
            fileInputStream = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            log.error("File not found exception: " + e.getStackTrace());
            return result;
        }

        try {
            objectInputStream = new ObjectInputStream(fileInputStream);
            result = (ArrayList<String>) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
        } catch (IOException e) {
            log.error("IO exception: " + e.getStackTrace());
            return result;
        } catch (ClassNotFoundException e) {
            log.error("Class not found exception: " + e.getStackTrace());
            return result;
        }

        return result;
    }

    public static boolean saveOrderToCsv(ArrayList<Order> orders, String csvFile, boolean makedHeader, List<String> whiteList) {

        try {
            FileWriter writer = new FileWriter(csvFile);

            //čas, coin, buy price, sell price, důvod sell, profit
            if (!makedHeader) {
                CSVUtils.writeLine(writer, Arrays.asList("Symbol", "BuyTime", "SellTime", "BuyPriceForUnitBTC", "SellPriceForUnitBTC", "SellReason", "PercentageProfitBTCWhitoutFee", "ProfitFeeIncluded", "PercentageProfitFeeIncluded", "WhiteList"));
                makedHeader = true;
            }

            for (Order order : orders) {
                boolean foundInWhiteList=false;

                List<String> list = new ArrayList<>();
                list.add(order.getSymbol());
                list.add(new Date(order.getBuyTime()).toString());
                list.add(new Date(order.getSellTime()).toString());
                list.add(String.format("%.9f", order.getBuyPriceBTCForUnit()));
                list.add(String.format("%.9f", order.getSellPriceBTCForUnit()));
                list.add(String.valueOf(order.getSellReason()));
                list.add(String.format("%.3f", order.getPercentageProfitBTCForUnitWithoutFee()));
                list.add(String.format("%.9f", order.getProfitFeeIncluded()));
                list.add(String.format("%.3f", order.getPercentageProfitFeeIncluded()));

                //TODO temporary solution
                for (String symbol : whiteList){
                    if(symbol.equals(order.getSymbol())){
                        foundInWhiteList = true;
                        break;
                    }
                }
                list.add(String.valueOf(foundInWhiteList));

                //CSVUtils.writeLine(writer, list);

                //try custom separator and quote.
                CSVUtils.writeLine(writer, list, ';', ' ');
            }

            writer.flush();
            writer.close();

        } catch (IOException e) {
            log.info("Exception when writing order to CSV " + e.getMessage());
        }

        return makedHeader;
    }
}
