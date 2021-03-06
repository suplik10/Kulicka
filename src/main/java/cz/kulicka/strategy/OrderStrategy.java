package cz.kulicka.strategy;

import cz.kulicka.entity.Order;
import cz.kulicka.entity.Ticker;

import java.util.List;

public interface OrderStrategy {

    boolean buy(Ticker ticker, double actualBTCUSDT);

    boolean rebuyStopLossProtection(Ticker ticker, double actualBTCUSDT);

    boolean sell(Order order, double actualBTCUSDT, double lastPriceBTC);

    boolean closeNonActiveOpenOrder(Order order);

    boolean instaSell(Order order, double actualBTCUSDT, double lastPriceBTC);

    void panicSellAll();
}
