package cz.kulicka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PropertyPlaceholder {

    @Value("${app.thread.sleep.between.requests.miliseconds}")
    private int threadSleepBetweenRequestsMiliseconds;

    @Value("${app.binance.api.key}")
    private String apiKey;

    @Value("${app.binance.secret.key}")
    private String secret;

    @Value("${app.exchange.buy.fee}")
    private double tradeBuyFee;

    @Value("${app.exchange.sell.fee}")
    private double tradeSellFee;

    @Value("${app.price.per.order.USD}")
    private double pricePerOrderUSD;


    public int getThreadSleepBetweenRequestsMiliseconds() {
        return threadSleepBetweenRequestsMiliseconds;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getSecret() {
        return secret;
    }

    public double getTradeBuyFee() {
        return tradeBuyFee;
    }

    public double getTradeSellFee() {
        return tradeSellFee;
    }

    public double getPricePerOrderUSD() {
        return pricePerOrderUSD;
    }
}
