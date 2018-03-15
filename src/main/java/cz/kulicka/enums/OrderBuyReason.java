package cz.kulicka.enums;

import javax.annotation.Nonnull;

public enum OrderBuyReason implements OrderEnum {

    MACD_BUY(0),
    STOPLOSS_PROTECTION_REBUY(1),
    MACD_BUY_IN_EMA_UPTREND(2);

    private final int cst;

    OrderBuyReason(final int cst) {
        this.cst = cst;
    }

    @Nonnull
    @Override
    public int getCST() {
        return cst;
    }
}