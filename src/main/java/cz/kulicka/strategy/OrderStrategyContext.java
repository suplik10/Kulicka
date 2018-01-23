package cz.kulicka.strategy;

import cz.kulicka.entity.Order;
import cz.kulicka.entity.Ticker;
import org.springframework.stereotype.Component;

@Component
public class OrderStrategyContext {

    OrderStrategy orderStrategy;

    public void setOrderStrategy(OrderStrategy strategy) {
        this.orderStrategy = strategy;
    }

    public boolean buy(Ticker ticker) {
        return orderStrategy.buy(ticker);
    }

    public boolean sell(Order order) {
        return orderStrategy.sell(order);
    }
}
