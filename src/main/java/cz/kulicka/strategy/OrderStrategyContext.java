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

    public boolean buy(Ticker ticker, double actualBTCUSDT) {
        return orderStrategy.buy(ticker, actualBTCUSDT);
    }

    public boolean rebuyStopLossProtection(Ticker ticker, double actualBTCUSDT) {
        return orderStrategy.rebuyStopLossProtection(ticker, actualBTCUSDT);
    }

    public boolean closeNonActiveOpenOrder(Order order) {
        return orderStrategy.closeNonActiveOpenOrder(order);
    }

    public boolean sell(Order order, double actualBTCUSDT, double lastPriceBTC) {
        return orderStrategy.sell(order, actualBTCUSDT, lastPriceBTC);
    }

    public boolean instaSellForProfit(Order order, double actualBTCUSDT, double lastPriceBTC) {
        return orderStrategy.instaSell(order, actualBTCUSDT, lastPriceBTC);
    }

    public void panicSellAll() {
        orderStrategy.panicSellAll();
    }
}
