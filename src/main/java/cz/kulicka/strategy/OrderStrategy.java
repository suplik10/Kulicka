package cz.kulicka.strategy;

import cz.kulicka.entity.Order;
import cz.kulicka.entity.Ticker;

import java.util.List;

public interface OrderStrategy {

    boolean buy(Ticker ticker, List<Order> activeOrders, double actualBTCUSDT);

    boolean sell(Order order, double actualSellPriceForOrderWithFee);
}
