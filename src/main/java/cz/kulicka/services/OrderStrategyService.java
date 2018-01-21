package cz.kulicka.services;

import cz.kulicka.entities.Order;
import cz.kulicka.entities.Ticker;

public interface OrderStrategyService {

    boolean firstTestingBuyStrategy(Ticker ticker);

    boolean firstTestingSellStrategy(Order order);
}
