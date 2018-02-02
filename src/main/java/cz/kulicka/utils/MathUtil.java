package cz.kulicka.utils;

public class MathUtil {

    public static double getPercentageProfit(double buyPrice, double actualPrice) {
        return (actualPrice / (buyPrice / 100)) - 100;
    }

    public static double getSellPriceForOrderWithFee(double boughtAmount, double tickerPricesForUnitUSDT, double sellFeeConstant){
        return (boughtAmount * tickerPricesForUnitUSDT) - ((boughtAmount * tickerPricesForUnitUSDT) * (sellFeeConstant / 100));
    }

}
