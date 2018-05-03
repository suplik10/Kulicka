package cz.kulicka.strategy.impl;

import com.google.common.collect.Iterables;
import cz.kulicka.PropertyPlaceholder;
import cz.kulicka.entity.Order;
import cz.kulicka.entity.Ticker;
import cz.kulicka.entity.TradingData;
import cz.kulicka.enums.OrderBuyReason;
import cz.kulicka.enums.OrderSellReason;
import cz.kulicka.exception.OrderApiException;
import cz.kulicka.service.BinanceApiService;
import cz.kulicka.service.MacdIndicatorService;
import cz.kulicka.service.OrderService;
import cz.kulicka.strategy.OrderStrategy;
import cz.kulicka.util.MathUtil;
import org.apache.log4j.Logger;

import java.util.List;

public class EMAStrategyImpl extends AbstractStrategy implements OrderStrategy {

	static Logger log = Logger.getLogger(EMAStrategyImpl.class);

	public EMAStrategyImpl(BinanceApiService binanceApiService, MacdIndicatorService macdIndicatorService, OrderService orderService, PropertyPlaceholder propertyPlaceholder) {
		super(binanceApiService, macdIndicatorService, orderService, propertyPlaceholder);
	}

	@Override
	public boolean buy(Ticker ticker, double actualBTCUSDT) {

		if (orderService.getAllOpenBySymbol(ticker.getSymbol()).size() > 0) {
			log.debug("Already open order for ticker symbol: " + ticker.getSymbol());
			return false;
		}

		log.debug("Try to make order for ticker symbol: " + ticker.getSymbol());

		TradingData tradingData = getEmaTradingDataHistorical(ticker.getSymbol(), propertyPlaceholder.getBinanceCandlesticksPeriod(), propertyPlaceholder.getEmaStrategyCandlestickCount(),
				propertyPlaceholder.getEmaStrategyShortEma(), propertyPlaceholder.getEmaStrategyLongEma(), propertyPlaceholder.isEmaBuyRemoveLastOpenCandlestick());

		// order ??
		if ((tradingData.getLastEmaShortYesterday() > tradingData.getLastEmaLongYesterday())
				&& (MathUtil.getPercentageDifference(tradingData.getLastEmaLongYesterday(), tradingData.getLastEmaShortYesterday()) - propertyPlaceholder.getEmaStrategyBuyLongIntolerantionPercentage() > 0)
				&& isUptrend(ticker, propertyPlaceholder.isCheckUptrendRemoveLastOpenCandlestick())
				&& isEmaCrossedUp(tradingData)) {

			emaCrossProtectedOrdersIds.add(makeOrder(ticker, actualBTCUSDT, tradingData, OrderBuyReason.EMA_BUY).getId());
			return true;
		}
		return false;
	}

	private boolean isEmaCrossedUp(TradingData tradingData) {

		if(!propertyPlaceholder.isEmaStrategyBuyWaitCross()){
			return true;
		}

		return tradingData.getPreLastEmaShortYesterday() < tradingData.getPrelastEmaLongYesterday();
	}

	@Override
	public boolean rebuyStopLossProtection(Ticker ticker, double actualBTCUSDT) {
		List<Order> openOrders = orderService.getAllOpenButNotActiveBySymbol(ticker.getSymbol());

		if (openOrders.size() == 0) {
			log.debug("RE-BUY - No open order for ticker: " + ticker.getSymbol());
			return false;
		}

		Order openOrder = Iterables.getFirst(openOrders, null);

		double lastPriceBTC = Double.parseDouble(binanceApiService.getLastPrice(openOrder.getSymbol()).getPrice());

		log.debug("RE-BUY - Try to make order, symbol: " + ticker.getSymbol());

		TradingData tradingData = getEmaTradingDataHistorical(ticker.getSymbol(), propertyPlaceholder.getBinanceCandlesticksPeriod(), propertyPlaceholder.getEmaStrategyCandlestickCount(),
				propertyPlaceholder.getEmaStrategyShortEma(), propertyPlaceholder.getEmaStrategyLongEma(), propertyPlaceholder.isStopLossProtectionBuyRemoveLastOpenCandlestick());

		if ((MathUtil.getPercentageDifference(openOrder.getSellPriceBTCForUnit(), lastPriceBTC) > propertyPlaceholder.getStopLossProtectionPercentageIntolerantion()) && isUptrend(ticker, propertyPlaceholder.isCheckUptrendRemoveLastOpenCandlestick())) {
			makeOrderByStopLossProtection(openOrder, tradingData, lastPriceBTC, actualBTCUSDT);
			return true;
		}
		return false;
	}

	private boolean sellByRequestPeriod(Order order, double actualBTCUSDT, double lastPriceBTC) {
		log.debug("Sell? order: " + order.toString());

		double actualPercentageProfitBTC = MathUtil.getPercentageDifference(order.getBuyPriceBTCForUnit(), lastPriceBTC);

		TradingData tradingData = getEmaTradingDataHistorical(order.getSymbol(), propertyPlaceholder.getBinanceCandlesticksPeriod(),
				propertyPlaceholder.getEmaCountCandlesticks(), propertyPlaceholder.getEmaStrategyShortEma(), propertyPlaceholder.getEmaStrategyLongEma(),
				propertyPlaceholder.isEmaSellRemoveLastOpenCandlestick());

		log.info("Sell? Symbol: " + order.getSymbol() + ", percentageProfitBTCWIthoutFee:  " + String.format("%.9f", actualPercentageProfitBTC) + " % "
				+ " EmaLastShort-open " + String.format("%.9f", tradingData.getLastEmaShortYesterday()) + " EmaLastLong-open " + String.format("%.9f", tradingData.getLastEmaLongYesterday()));

		if (propertyPlaceholder.isTrailingStopStrategy() && order.isTrailingStop()) {
			return handleTrailingStopOrder(order, actualBTCUSDT, actualPercentageProfitBTC, lastPriceBTC, tradingData);
		} else {
			if (actualPercentageProfitBTC > propertyPlaceholder.getTakeProfitPercentage() && !isEmaCrossedDown(tradingData, order.getId())) {
				if (propertyPlaceholder.isTrailingStopStrategy()) {
					log.debug("Border CRACKED! but trailing stop enabled for symbol: " + order.getSymbol());
					order.setTrailingStop(true);
					order.setTrailingStopTakeProfitPercentage(actualPercentageProfitBTC + propertyPlaceholder.getTrailingStopTakeProfitPlusPercentageConstant());
					order.setTrailingStopStopLossPercentage(actualPercentageProfitBTC + propertyPlaceholder.getTrailingStopStopLossMinusPercentageConstant());
					return false;
				}
				log.info("Border CRACKED! SELL AND GET MY MONEY!!!");
				setOrderForSell(order, actualBTCUSDT, actualPercentageProfitBTC, OrderSellReason.CANDLESTICK_PERIOD_TAKE_PROFIT, lastPriceBTC, true);
				return true;
			} else if (actualPercentageProfitBTC < propertyPlaceholder.getStopLossPercentage() || isEmaCrossedDown(tradingData, order.getId())) {

				//TODO check if anytime happend ?!
				if (!isEmaCrossedDown(tradingData, order.getId()) && !propertyPlaceholder.isStopLossProtection()) {
					log.info("HODL over last closed short ema was smaller than long, but last open short ema is highter than long - protect rebuy");
					log.info("Percengate profit BTC: " + actualPercentageProfitBTC);
					log.info("Pre last short ema - closed: " + tradingData.getPreLastEmaShortYesterday());
					log.info("Pre last long ema - closed: " + tradingData.getPrelastEmaLongYesterday());
					return false;
				}

				if (isEmaCrossedDown(tradingData, order.getId())) {
					log.info("PANIC SELL!!! - EMA CROSSED DOWN");
					setOrderForSell(order, actualBTCUSDT, actualPercentageProfitBTC, OrderSellReason.CANDLESTICK_PERIOD_CROSS_DOWN_EMA, lastPriceBTC, true);
				} else {
					log.info("PANIC SELL!!! - STOPLOSS");
					setOrderForSell(order, actualBTCUSDT, actualPercentageProfitBTC, OrderSellReason.CANDLESTICK_PERIOD_STOPLOSS, lastPriceBTC, !propertyPlaceholder.isStopLossProtection());
				}

				return true;
			} else {
				//HODL, HODL, HOOOOODDDDLLLLLLLLL!!!
				return false;
			}
		}
	}

	protected boolean handleInstaSell(Order order, double actualBTCUSDT, double lastPriceBTC) {

		TradingData tradingData = getEmaTradingDataHistorical(order.getSymbol(), propertyPlaceholder.getBinanceCandlesticksPeriod(),
				propertyPlaceholder.getEmaCountCandlesticks(), propertyPlaceholder.getEmaStrategyShortEma(), propertyPlaceholder.getEmaStrategyLongEma(),
				propertyPlaceholder.isEmaSellRemoveLastOpenCandlestick());

		double actualPercentageProfitBTC = MathUtil.getPercentageDifference(order.getBuyPriceBTCForUnit(), lastPriceBTC);

		if (propertyPlaceholder.isTrailingStopStrategy() && order.isTrailingStop()) {
			return handleTrailingStopOrder(order, actualBTCUSDT, actualPercentageProfitBTC, lastPriceBTC, tradingData);
		} else {
			if (actualPercentageProfitBTC > propertyPlaceholder.getTakeProfitInstaSellPercentage() && !isEmaCrossedDown(tradingData, order.getId())) {
				log.info("INSTA SELL!!! - TAKE PROFIT");
				if (propertyPlaceholder.isTrailingStopStrategy()) {
					log.debug("INSTA SELL set TRAILING STOP for symbol: " + order.getSymbol());
					order.setTrailingStop(true);
					order.setTrailingStopTakeProfitPercentage(actualPercentageProfitBTC + propertyPlaceholder.getTrailingStopTakeProfitPlusPercentageConstant());
					order.setTrailingStopStopLossPercentage(actualPercentageProfitBTC + propertyPlaceholder.getTrailingStopStopLossMinusPercentageConstant());
					return false;
				}
				setOrderForSell(order, actualBTCUSDT, actualPercentageProfitBTC, OrderSellReason.INSTA_SELL_TAKE_PROFIT, lastPriceBTC, true);
				return true;
			} else if ((actualPercentageProfitBTC < propertyPlaceholder.getStopLossPercentage() || sellByStopLostProtection(order, lastPriceBTC)) && !isEmaCrossedDown(tradingData, order.getId())) {
				log.info("INSTA SELL!!! - STOPLOSS");
				setOrderForSell(order, actualBTCUSDT, actualPercentageProfitBTC, OrderSellReason.INSTA_SELL_STOPLOSS, lastPriceBTC, !propertyPlaceholder.isStopLossProtection());
				return true;
			} else if (isEmaCrossedDown(tradingData, order.getId())) {
				setOrderForSell(order, actualBTCUSDT, actualPercentageProfitBTC, OrderSellReason.INSTA_SELL_CROSS_DOWN_EMA, lastPriceBTC, !propertyPlaceholder.isStopLossProtection());
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public boolean sell(Order order, double actualBTCUSDT, double lastPriceBTC) {
		emaCrossProtectedOrdersIds.clear();
		log.debug("emaCrossProtectedOrdersIds " + emaCrossProtectedOrdersIds.toString());

		boolean sellCoin = sellByRequestPeriod(order, actualBTCUSDT, lastPriceBTC);

		if (sellCoin && propertyPlaceholder.isCoinMachineOn()) {
			try {
				makeServerSellOrder(order);
			} catch (OrderApiException e) {
				return false;
			}
		}

		orderService.update(order);

		return sellCoin;
	}

	@Override
	public boolean closeNonActiveOpenOrder(Order order) {
		TradingData tradingData = getEmaTradingDataHistorical(order.getSymbol(), propertyPlaceholder.getBinanceCandlesticksPeriod(),
				propertyPlaceholder.getEmaCountCandlesticks(), propertyPlaceholder.getEmaStrategyShortEma(), propertyPlaceholder.getEmaStrategyLongEma(),
				propertyPlaceholder.isStopLossProtectionCloseNonActiveRemoveLastOpenCandlestick());
		return tradingData.getLastEmaShortYesterday() < tradingData.getLastEmaLongYesterday() ? true : false;
	}

	@Override
	public boolean instaSell(Order order, double actualBTCUSDT, double lastPriceBTC) {
		boolean sellCoin = handleInstaSell(order, actualBTCUSDT, lastPriceBTC);

		if (sellCoin && propertyPlaceholder.isCoinMachineOn()) {
			try {
				makeServerSellOrder(order);
			} catch (OrderApiException e) {
				return false;
			}
		}

		orderService.update(order);

		return sellCoin;
	}

	@Override
	public void panicSellAll() {
		List<Order> activeOrders = orderService.getAllActive();

		log.info("================================================================================================================");
		log.info("========== Handle PANIC SELL ALL orders start > " + activeOrders.size() + " active orders ======================");
		log.info("================================================================================================================");

		for (Order order : activeOrders) {
			try {
				makeServerSellOrder(order);
			} catch (OrderApiException e) {
				log.error(e);
			}
		}
	}
}
