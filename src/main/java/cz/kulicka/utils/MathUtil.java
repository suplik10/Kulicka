package cz.kulicka.utils;

public class MathUtil {

    public static double getPercentageProfit(double buyPrice, double actualPrice) {
        return (actualPrice / (buyPrice / 100)) - 100;
    }
}
