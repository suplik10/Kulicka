package cz.kulicka.services;

import cz.kulicka.entities.Order;
import cz.kulicka.entities.Ticker;

public interface OrderStrategyService {

    boolean firstDumbBuyStrategy(Ticker ticker);

    boolean firstDumbSellStrategy(Order order);

    boolean secondTestingBuyStrategy(Ticker ticker);

    boolean secondDumbSellStrategyWithStopLoss(Order order);
}
