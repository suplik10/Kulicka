package cz.kulicka.strategy;

import cz.kulicka.entity.Order;
import cz.kulicka.entity.Ticker;

import java.util.List;

public interface OrderStrategy {

    boolean buy(Ticker ticker, double actualBTCUSDT);

    boolean sell(Order order, double actualSellPriceForOrderWithFee);

    boolean instaSell(Order order, double actualSellPriceForOrderWithFee);
}
