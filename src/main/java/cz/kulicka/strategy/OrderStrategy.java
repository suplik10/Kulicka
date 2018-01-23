package cz.kulicka.strategy;

import cz.kulicka.entity.Order;
import cz.kulicka.entity.Ticker;

public interface OrderStrategy {

    boolean buy(Ticker ticker);

    boolean sell(Order order);
}
