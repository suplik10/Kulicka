package cz.kulicka.enums;

import javax.annotation.Nonnull;

public enum OrderSellReason implements OrderEnum {

    CANDLESTICK_PERIOD_TAKE_PROFIT(0),

    CANDLESTICK_PERIOD_STOPLOSS(1),

    CANDLESTICK_PERIOD_NEGATIVE_MACD(2),

    INSTA_SELL_TAKE_PROFIT(3),

    INSTA_SELL_STOPLOSS(4),

    INSTA_SELL_TRAILING_STOP_STOPLOSS(5);


    private final int cst;

    OrderSellReason(final int cst) {
        this.cst = cst;
    }

    @Nonnull
    @Override
    public int getCST() {
        return cst;
    }
}
