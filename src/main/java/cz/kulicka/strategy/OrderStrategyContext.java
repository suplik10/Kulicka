package cz.kulicka.strategy;

import cz.kulicka.entity.Order;
import cz.kulicka.entity.Ticker;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderStrategyContext {

    OrderStrategy orderStrategy;

    public void setOrderStrategy(OrderStrategy strategy) {
        this.orderStrategy = strategy;
    }

    public boolean buy(Ticker ticker, List<Order> activeOrders) {
        return orderStrategy.buy(ticker, activeOrders);
    }

    public boolean sell(Order order) {
        return orderStrategy.sell(order);
    }
}
