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

    public boolean buy(Ticker ticker, double actualBTCUSDT) {
        return orderStrategy.buy(ticker, actualBTCUSDT);
    }

    public boolean sell(Order order, double actualSellPriceForOrderWithFee) {
        return orderStrategy.sell(order, actualSellPriceForOrderWithFee);
    }

    public boolean instaSellForProfit(Order order, double actualSellPriceForOrderWithFee) {
        return orderStrategy.instaSell(order, actualSellPriceForOrderWithFee);
    }
}
