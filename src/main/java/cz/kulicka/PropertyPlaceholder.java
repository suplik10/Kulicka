package cz.kulicka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PropertyPlaceholder {

    @Value("${app.time.difference.between.requests.minutes}")
    private int timeDifferenceBetweenRequestsInMinutes;

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

    @Value("${app.strategy.macd.ema.long}")
    private int emaLongConstant;

    @Value("${app.strategy.macd.ema.short}")
    private int emaShortConstant;

    @Value("${app.strategy.macd.ema.signal}")
    private int emaSignalConstant;

    @Value("${app.binance.candlesticks.period}")
    private String binanceCandlesticksPeriod;

    @Value("${app.strategy.macd.ema.count.candlesticks}")
    private int emaCountCandlesticks;

    @Value("${app.strategy.macd.take.profit.percentage}")
    private double takeProfitPercentage;

    @Value("${app.strategy.macd.take.profit.insta.sell.percentage}")
    private double takeProfitInstaSellPercentage;

    @Value("${app.strategy.macd.stop.loss.percentage}")
    private double stopLossPercentage;

    @Value("${app.csv.report.file.path}")
    private String csvReportFilePath;

    @Value("#{'${app.exchange.coins.whitelist}'.split(',')}")
    private List<String> whiteListCoins;

    public int getTimeDifferenceBetweenRequestsInMinutes() {
        return timeDifferenceBetweenRequestsInMinutes;
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

    public int getEmaLongConstant() {
        return emaLongConstant;
    }

    public int getEmaShortConstant() {
        return emaShortConstant;
    }

    public int getEmaSignalConstant() {
        return emaSignalConstant;
    }

    public String getBinanceCandlesticksPeriod() {
        return binanceCandlesticksPeriod;
    }

    public int getEmaCountCandlesticks() {
        return emaCountCandlesticks;
    }

    public double getTakeProfitPercentage() {
        return takeProfitPercentage;
    }

    public double getStopLossPercentage() {
        return stopLossPercentage;
    }

    public String getCsvReportFilePath() {
        return csvReportFilePath;
    }

    public double getTakeProfitInstaSellPercentage() {
        return takeProfitInstaSellPercentage;
    }

    public List<String> getWhiteListCoins() {
        return whiteListCoins;
    }
}
