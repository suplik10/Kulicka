package cz.kulicka.enums;

import javax.annotation.Nonnull;

public enum OrderBuyReason implements OrderEnum {

    MACD_BUY(0),
    STOPLOSS_PROTECTION_REBUY(1),
    EMA_BUY(2);

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