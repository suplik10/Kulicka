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

    @Value("${fileNamePostFix}")
    private String appName;

    @Value("${app.binance.secret.key}")
    private String secret;

    @Value("${app.exchange.buy.fee}")
    private double tradeBuyFee;

    @Value("${app.exchange.sell.fee}")
    private double tradeSellFee;

    @Value("${app.price.per.order.USD}")
    private double pricePerOrderUSD;

    @Value("${app.price.per.order.BTC}")
    private double pricePerOrderBTC;

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

    @Value("${app.strategy.trailing.stop.take.profit.plus.percentage.constant}")
    private double trailingStopTakeProfitPlusPercentageConstant;

    @Value("${app.strategy.trailing.stop.stoploss.minus.percentage.constant}")
    private double trailingStopStopLossMinusPercentageConstant;

    @Value("${app.csv.report.finished.orders.file.path}")
    private String csvReportFilePath;

    @Value("${app.csv.report.open.orders.file.path}")
    private String csvReportOpenOrdersFilePath;

    @Value("${app.csv.report.daily.file.path}")
    private String csvReportDailyFilePath;

    @Value("${app.exchange.coins.blacklist.ignore}")
    private boolean ignoreBlacklist;

    @Value("${app.strategy.stoploss.protection}")
    private boolean stopLossProtection;

    @Value("${app.strategy.ema.buy.candlesticks.remove.last.open}")
    private boolean emaBuyRemoveLastOpenCandlestick;

    @Value("${app.strategy.stoploss.protection.buy.remove.last.open}")
    private boolean stopLossProtectionBuyRemoveLastOpenCandlestick;

    @Value("${app.strategy.stoploss.protection.close.non.active.remove.last.open}")
    private boolean stopLossProtectionCloseNonActiveRemoveLastOpenCandlestick;

    @Value("${app.strategy.ema.uptrend.remove.last.open}")
    private boolean checkUptrendRemoveLastOpenCandlestick;

    @Value("${app.strategy.ema.sell.candlesticks.remove.last.open}")
    private boolean emaSellRemoveLastOpenCandlestick;

    @Value("${app.strategy.ema.long.sell.intoleration.percentage}")
    private double emaStrategySellLongIntolerantionPercentage;

    @Value("${app.exchange.order.allow.new}")
    private boolean allowNewOrders;

    @Value("${app.strategy.stoploss.protection.percentage.intolerantion}")
    private double stopLossProtectionPercentageIntolerantion;

    @Value("${app.strategy.ema.check.uptrend}")
    private boolean checkUptrendEmaStrategy;

    @Value("${app.binance.coin.machine.on}")
    private boolean coinMachineOn;

    @Value("${app.strategy.trailing.stop}")
    private boolean trailingStopStrategy;

    @Value("${app.strategy.ema.uptrend.ema.candlestick.period}")
    private String emaUptrendEmaStrategyCandlestickPeriod;

    @Value("${app.strategy.selected.strategy}")
    private String actualStrategy;

    @Value("${app.strategy.ema.uptrend.short.ema}")
    private int emaUptrendEmaStrategyShortEma;

    @Value("${app.strategy.ema.uptrend.long.ema}")
    private int emaUptrendEmaStrategyLongEma;

    @Value("${app.strategy.ema.short}")
    private int emaStrategyShortEma;

    @Value("${app.strategy.ema.long}")
    private int emaStrategyLongEma;

    @Value("${app.strategy.ema.candlestick.count}")
    private int emaStrategyCandlestickCount;

    @Value("${app.strategy.ema.long.buy.intoleration.percentage}")
    private double emaStrategyBuyLongIntolerantionPercentage;

    @Value("#{'${app.exchange.coins.blacklist}'.split(',')}")
    private List<String> blackListCoins;

    @Value("#{'${app.exchange.coins.whitelist}'.split(',')}")
    private List<String> whitelistCoins;

    @Value("${app.exchange.coins.whitelist.ignore}")
    private boolean ignoreWhitelist;

    @Value("#{'${app.notification.emails}'.split(',')}")
    private List<String> notificationEmails;

    @Value("${app.notification.on.error.enabled}")
    private boolean notificationOnErrorEnabled;

    @Value("${app.strategy.ema.uptrend.ema.candlestick.count}")
    private int emaUptrendEmaStrategyCandlestickCount;

    @Value("${app.strategy.ema.buy.wait.cross}")
    private boolean emaStrategyBuyWaitCross;

    @Value("${app.strategy.ema.trailing.stop.after.cross.down}")
    private boolean setTrailingStopAfterEmaCrossedDown;

	@Value("${app.exchange.synch.date.time.programically}")
	private boolean synchDateTimeProgramically;

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

    public boolean isStopLossProtection() {
        return stopLossProtection;
    }

    public boolean isCheckUptrendEmaStrategy() {
        return checkUptrendEmaStrategy;
    }

    public String getEmaUptrendEmaStrategyCandlestickPeriod() {
        return emaUptrendEmaStrategyCandlestickPeriod;
    }

    public int getEmaUptrendEmaStrategyShortEma() {
        return emaUptrendEmaStrategyShortEma;
    }

    public int getEmaUptrendEmaStrategyLongEma() {
        return emaUptrendEmaStrategyLongEma;
    }

    public int getEmaUptrendEmaStrategyCandlestickCount() {
        return emaUptrendEmaStrategyCandlestickCount;
    }

    public boolean isTrailingStopStrategy() {
        return trailingStopStrategy;
    }

    public double getTrailingStopTakeProfitPlusPercentageConstant() {
        return trailingStopTakeProfitPlusPercentageConstant;
    }

    public double getStopLossProtectionPercentageIntolerantion() {
        return stopLossProtectionPercentageIntolerantion;
    }

    public String getActualStrategy() {
        return actualStrategy;
    }

    public int getEmaStrategyShortEma() {
        return emaStrategyShortEma;
    }

    public int getEmaStrategyLongEma() {
        return emaStrategyLongEma;
    }

    public int getEmaStrategyCandlestickCount() {
        return emaStrategyCandlestickCount;
    }

    public double getEmaStrategyBuyLongIntolerantionPercentage() {
        return emaStrategyBuyLongIntolerantionPercentage;
    }

    public double getTrailingStopStopLossMinusPercentageConstant() {
        return trailingStopStopLossMinusPercentageConstant;
    }

    public double getPricePerOrderBTC() {
        return pricePerOrderBTC;
    }

    public boolean isCoinMachineOn() {
        return coinMachineOn;
    }

    public String getAppName() {
        return appName;
    }

    public String getCsvReportOpenOrdersFilePath() {
        return csvReportOpenOrdersFilePath;
    }

    public String getCsvReportDailyFilePath() {
        return csvReportDailyFilePath;
    }

    public boolean isIgnoreBlacklist() {
        return ignoreBlacklist;
    }

    public boolean isAllowNewOrders() {
        return allowNewOrders;
    }

    public List<String> getBlackListCoins() {
        return blackListCoins;
    }

    public void setAllowNewOrders(boolean allowNewOrders) {
        this.allowNewOrders = allowNewOrders;
    }

    public boolean isEmaBuyRemoveLastOpenCandlestick() {
        return emaBuyRemoveLastOpenCandlestick;
    }

    public boolean isStopLossProtectionBuyRemoveLastOpenCandlestick() {
        return stopLossProtectionBuyRemoveLastOpenCandlestick;
    }

    public boolean isCheckUptrendRemoveLastOpenCandlestick() {
        return checkUptrendRemoveLastOpenCandlestick;
    }

    public boolean isEmaSellRemoveLastOpenCandlestick() {
        return emaSellRemoveLastOpenCandlestick;
    }

    public double getEmaStrategySellLongIntolerantionPercentage() {
        return emaStrategySellLongIntolerantionPercentage;
    }

    public boolean isStopLossProtectionCloseNonActiveRemoveLastOpenCandlestick() {
        return stopLossProtectionCloseNonActiveRemoveLastOpenCandlestick;
    }

    public boolean isEmaStrategyBuyWaitCross() {
        return emaStrategyBuyWaitCross;
    }

    public List<String> getNotificationEmails() {
        return notificationEmails;
    }

    public boolean isNotificationOnErrorEnabled() {
        return notificationOnErrorEnabled;
    }

    public boolean isSetTrailingStopAfterEmaCrossedDown() {
        return setTrailingStopAfterEmaCrossedDown;
    }

    public List<String> getWhitelistCoins() {
        return whitelistCoins;
    }

    public boolean isIgnoreWhitelist() {
        return ignoreWhitelist;
    }

	public boolean isSynchDateTimeProgramically() {
		return synchDateTimeProgramically;
	}
}

