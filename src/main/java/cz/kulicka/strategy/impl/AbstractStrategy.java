package cz.kulicka.strategy.impl;

import com.binance.api.client.constant.BinanceApiConstants;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.AssetBalance;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.exception.BinanceApiException;
import cz.kulicka.CoreEngine;
import cz.kulicka.PropertyPlaceholder;
import cz.kulicka.constant.CommonConstants;
import cz.kulicka.entity.MacdIndicator;
import cz.kulicka.entity.Order;
import cz.kulicka.entity.Ticker;
import cz.kulicka.entity.TradingData;
import cz.kulicka.enums.OrderBuyReason;
import cz.kulicka.enums.OrderSellReason;
import cz.kulicka.exception.OrderApiException;
import cz.kulicka.service.BinanceApiServiceMKA;
import cz.kulicka.service.MacdIndicatorService;
import cz.kulicka.service.OrderService;
import cz.kulicka.util.CommonUtil;
import cz.kulicka.util.DateTimeUtils;
import cz.kulicka.util.MathUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class AbstractStrategy {

	static Logger log = Logger.getLogger(AbstractStrategy.class);

	protected BinanceApiServiceMKA binanceApiServiceMKA;
	protected MacdIndicatorService macdIndicatorService;
	protected PropertyPlaceholder propertyPlaceholder;
	protected OrderService orderService;

	protected ArrayList<Long> emaCrossProtectedOrdersIds = new ArrayList<>();

	protected AbstractStrategy(BinanceApiServiceMKA binanceApiServiceMKA, MacdIndicatorService macdIndicatorService,
							   OrderService orderService, PropertyPlaceholder propertyPlaceholder) {
		this.binanceApiServiceMKA = binanceApiServiceMKA;
		this.macdIndicatorService = macdIndicatorService;
		this.orderService = orderService;
		this.propertyPlaceholder = propertyPlaceholder;
	}

	protected Order setOrderForSell(Order order, double actualBTCUSDT, double actualPercentageProfitBTC,
									OrderSellReason orderSellReason, double lastPriceBTC, boolean closeOrder) {
		double actualSellPriceForOrderWithFee = MathUtil.getSellPriceForOrderWithFee(order.getBoughtAmount(),
				lastPriceBTC * actualBTCUSDT, order.getSellFeeConstant());
		double actualPercentageProfit = MathUtil.getPercentageDifference(order.getBuyPriceForOrderWithFee(),
				actualSellPriceForOrderWithFee);

		order.setPercentageProfitFeeIncluded(actualPercentageProfit);
		order.setPercentageProfitBTCForUnitWithoutFee(actualPercentageProfitBTC);
		order.setSellPriceForOrderWithFee(actualSellPriceForOrderWithFee);
		order.setProfitFeeIncluded(order.getSellPriceForOrderWithFee() - order.getBuyPriceForOrderWithFee());
		order.setSellTime(System.currentTimeMillis());
		order.setSellReason(orderSellReason.getCST());
		order.setSellPriceBTCForUnit(lastPriceBTC);
		order.setOpen(!closeOrder);
		order.setActive(false);

		return order;
	}

	protected boolean sellByStopLostProtection(Order order, double lastPriceBTC) {
		if (!propertyPlaceholder.isStopLossProtection()) {
			return false;
		}

		if (lastPriceBTC > order.getStopLossPriceValue()) {
			return false;
		}

		return true;
	}

	protected TradingData getFullTradingDataHistorical(String symbol, String candlestickPeriod, int candlecticksCount,
													   int emaShort, int emaLong, int emaSignal, boolean
															   removeLastOpenCandlestick) {
		return MathUtil.getTradingData(symbol, null, getCandlesticksValues(symbol, candlestickPeriod,
				candlecticksCount, removeLastOpenCandlestick), emaShort, emaLong, emaSignal,
				0, 0, 0);
	}

	protected TradingData getEmaTradingDataHistorical(String symbol, String candlestickPeriod, int candlecticksCount,
													  int emaShort, int emaLong, boolean removeLastOpenCandlestick) {
		TradingData tradingData = MathUtil.getEmaShortLongTradingData(symbol, null, getCandlesticksValues(symbol,
				candlestickPeriod, candlecticksCount, removeLastOpenCandlestick), emaShort, emaLong,
				0, 0);
		log.debug(tradingData.toString());
		return tradingData;
	}

	protected TradingData getEmaTradingData(Order order, boolean removeLastOpenCandlestick) {

		MacdIndicator macdIndicator = macdIndicatorService.getMacdIndicatorByOrderId(order.getId());

		TradingData tradingData = MathUtil.getEmaShortLongTradingData(order.getSymbol(), null, getCandlesticksValues
						(order.getSymbol(), propertyPlaceholder.getBinanceCandlesticksPeriod(),
								2, removeLastOpenCandlestick), propertyPlaceholder.getEmaStrategyShortEma(),
				propertyPlaceholder
						.getEmaStrategyLongEma(),
				macdIndicator.getEmaShortYesterday(), macdIndicator.getEmaLongYesterday());

		log.debug(tradingData.toString());

		macdIndicator.setEmaShortYesterday(tradingData.getLastEmaShortYesterday());
		macdIndicator.setEmaLongYesterday(tradingData.getLastEmaLongYesterday());
		macdIndicatorService.update(macdIndicator);

		log.debug(macdIndicator.toString());

		return tradingData;
	}

	protected TradingData checkMacdIndicatorAndGetTradingData(Order order, boolean removeLastOpenCandlestick) {

		MacdIndicator macdIndicator = macdIndicatorService.getMacdIndicatorByOrderId(order.getId());

		log.debug(macdIndicator.toString());

		TradingData tradingData = MathUtil.getTradingData(order.getSymbol(), order.getId(), getCandlesticksValues
						(order.getSymbol(), propertyPlaceholder.getBinanceCandlesticksPeriod(), 2,
								removeLastOpenCandlestick),
				propertyPlaceholder.getEmaShortConstant(), propertyPlaceholder.getEmaLongConstant(),
				propertyPlaceholder.getEmaSignalConstant(),
				macdIndicator.getEmaShortYesterday(), macdIndicator.getEmaLongYesterday(), macdIndicator
						.getEmaSignalYesterday());

		log.debug(tradingData.toString());

		macdIndicator.setEmaShortYesterday(tradingData.getPreLastEmaShortYesterday());
		macdIndicator.setEmaLongYesterday(tradingData.getPrelastEmaLongYesterday());
		macdIndicator.setEmaSignalYesterday(tradingData.getPreLastEmaSignalYesterday());
		macdIndicator.getMacdList().add(tradingData.getPreLastMacdHistogram());
		macdIndicatorService.update(macdIndicator);

		log.debug(macdIndicator.toString());

		return tradingData;
	}

	protected boolean isUptrend(String symbol, boolean removeLastOpenCandlestick) {

		if (!propertyPlaceholder.isCheckUptrendEmaStrategy()) {
			return true;
		}

		log.debug("IS uptrend for ticker: " + symbol);

		TradingData tradingData = getEmaTradingDataHistorical(symbol, propertyPlaceholder
						.getEmaUptrendEmaStrategyCandlestickPeriod(),
				propertyPlaceholder.getEmaUptrendEmaStrategyCandlestickCount(), propertyPlaceholder
						.getEmaUptrendEmaStrategyShortEma(),
				propertyPlaceholder.getEmaUptrendEmaStrategyLongEma(), removeLastOpenCandlestick);

		log.debug(tradingData);

		return tradingData.getLastEmaShortYesterday() > tradingData.getLastEmaLongYesterday() ? true : false;
	}

	protected boolean handleTrailingStopOrder(Order order, double actualBTCUSDT, double actualPercentageProfitBTC,
											  double lastPriceBTC, TradingData tradingData) {

		log.debug("TRAILING STOP for symbol: " + order.getSymbol() + " actualPercentageProfitBTC: " + String.format
				("%.3f", actualPercentageProfitBTC)
				+ " % actual TAKEPROFIT " + String.format("%.3f", order.getTrailingStopTakeProfitPercentage())
				+ " % actual STOPLOSS " + String.format("%.3f", order.getTrailingStopStopLossPercentage()) + " %");

		if (actualPercentageProfitBTC > order.getTrailingStopTakeProfitPercentage() && !isEmaCrossedDown(tradingData,
				order, actualPercentageProfitBTC)) {
			order.setTrailingStopTakeProfitPercentage(actualPercentageProfitBTC + propertyPlaceholder
					.getTrailingStopTakeProfitPlusPercentageConstant());
			order.setTrailingStopStopLossPercentage(actualPercentageProfitBTC + propertyPlaceholder
					.getTrailingStopStopLossMinusPercentageConstant());
			return false;
		} else if (actualPercentageProfitBTC < order.getTrailingStopStopLossPercentage() && !isEmaCrossedDown
				(tradingData, order, actualPercentageProfitBTC) && !isBuyConditionPassed(tradingData)) {
			setOrderForSell(order, actualBTCUSDT, actualPercentageProfitBTC, OrderSellReason
					.INSTA_SELL_TRAILING_STOP_STOPLOSS, lastPriceBTC, !propertyPlaceholder.isStopLossProtection());
			return true;
		} else if (isEmaCrossedDown(tradingData, order, actualPercentageProfitBTC) && !isBuyConditionPassed
				(tradingData)) {
			setOrderForSell(order, actualBTCUSDT, actualPercentageProfitBTC, OrderSellReason
					.TRAILING_STOP_CROSS_DOWN_EMA, lastPriceBTC, !propertyPlaceholder.isStopLossProtection());
			return true;
		}
		return false;
	}

	protected boolean isEmaCrossedDown(TradingData tradingData, Order order, double actualPercentageProfitBTC) {

		if (propertyPlaceholder.isEmaSellRemoveLastOpenCandlestick()) {
			log.debug("orderId" + order.getId() + "emaCrossProtectedOrdersIds " + emaCrossProtectedOrdersIds.toString
					());
			for (long id : emaCrossProtectedOrdersIds) {
				if (order.getId() == id) {
					return false;
				}
			}
		}

		log.warn(" long " + String.format("%.9f", tradingData.getLastEmaLongYesterday()) + " short "
				+ String.format("%.9f", tradingData.getLastEmaShortYesterday()) + " intol" + propertyPlaceholder
				.getEmaStrategySellLongIntolerantionPercentage());

		boolean emaCrossedDown = (MathUtil.getPercentageDifference(tradingData.getLastEmaLongYesterday(), tradingData
				.getLastEmaShortYesterday()) + propertyPlaceholder.getEmaStrategySellLongIntolerantionPercentage())
				< 0;

		if (propertyPlaceholder.isSetTrailingStopAfterEmaCrossedDown() && emaCrossedDown) {
			if (!order.isTrailingStop()) {
				order.setTrailingStop(true);
				order.setTrailingStopTakeProfitPercentage(actualPercentageProfitBTC + propertyPlaceholder
						.getTrailingStopTakeProfitPlusPercentageConstant());
				order.setTrailingStopStopLossPercentage(actualPercentageProfitBTC + propertyPlaceholder
						.getTrailingStopStopLossMinusPercentageConstant());
			}
			emaCrossedDown = false;
		}

		return emaCrossedDown;
	}

	protected Order makeOrder(Ticker ticker, double actualBTCUSDT, TradingData tradingData, OrderBuyReason buyReason) {
		log.debug("Make order for symbol: " + ticker.getSymbol());

		double lastPriceBTC = Double.parseDouble(binanceApiServiceMKA.getLastPrice(ticker.getSymbol()).getPrice());
		double lastPriceInUSDT = lastPriceBTC * actualBTCUSDT;
		double quantity = 0;

		//TODO important !!! aby naše nabídka byla první na řadě
		if (propertyPlaceholder.isCoinMachineOn()) {
			try {
				quantity = makeServerBuyOrder(ticker.getSymbol(), lastPriceBTC);
			} catch (OrderApiException e) {
				return null;
			}
		}

		Order newOrder = new Order(ticker.getSymbol(), System.currentTimeMillis(), propertyPlaceholder
				.getPricePerOrderUSD(), lastPriceInUSDT,
				propertyPlaceholder.getTradeBuyFee(), propertyPlaceholder.getTradeSellFee(), lastPriceBTC, buyReason
				.getCST());
		newOrder.setAmount(quantity);
		newOrder.setActive(true);
		newOrder.setOpen(true);

		Order orderWithId = orderService.create(newOrder);

		ArrayList<Float> macdList = new ArrayList<>();
		macdList.add(tradingData.getPreLastMacdHistogram());
		//add only finished histogram candle
		//macdList.add(tradingData.getLastMacdHistogram());

		MacdIndicator macdIndicator = new MacdIndicator();
		macdIndicator.setBuyTime(orderWithId.getBuyTime());
		macdIndicator.setMacdList(macdList);
		macdIndicator.setOrderId(orderWithId.getId());
		macdIndicator.setMacdBuy(tradingData.getLastMacdHistogram());
		macdIndicator.setSymbol(orderWithId.getSymbol());
		//add only finished histogram candle data
		macdIndicator.setEmaLongYesterday(tradingData.getLastEmaLongYesterday());
		macdIndicator.setEmaShortYesterday(tradingData.getLastEmaShortYesterday());
		macdIndicator.setEmaSignalYesterday(tradingData.getLastEmaSignalYesterday());

		log.debug(macdIndicator.toString());

		macdIndicatorService.create(macdIndicator);

		return newOrder;
	}

	protected boolean isBuyConditionPassed(TradingData tradingData) {

		if ((tradingData.getLastEmaShortYesterday() > tradingData.getLastEmaLongYesterday())
				&& (MathUtil.getPercentageDifference(tradingData.getLastEmaLongYesterday(), tradingData
				.getLastEmaShortYesterday()) - propertyPlaceholder.getEmaStrategyBuyLongIntolerantionPercentage() > 0)
				&& isUptrend(tradingData.getSymbol(), propertyPlaceholder.isCheckUptrendRemoveLastOpenCandlestick())
				&& isEmaCrossedUp(tradingData)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isEmaCrossedUp(TradingData tradingData) {

		if (!propertyPlaceholder.isEmaStrategyBuyWaitCross()) {
			return true;
		}

		return tradingData.getPreLastEmaShortYesterday() < tradingData.getPrelastEmaLongYesterday();
	}

	protected void makeOrderByStopLossProtection(Order openOrder, TradingData tradingData, double lastPriceBTC, double
			actualBTCUSDT) {
		log.debug("RE-BUY - Make order macd, symbol: " + tradingData.toString() + " parent id: " + openOrder.getId());

		double lastPriceInUSDT = lastPriceBTC * actualBTCUSDT;
		double quantity = 0;

		if (propertyPlaceholder.isCoinMachineOn()) {
			try {
				quantity = makeServerBuyOrder(openOrder.getSymbol(), lastPriceBTC);
			} catch (OrderApiException e) {
				return;
			}
		}

		Order newOrder = new Order(openOrder.getSymbol(), System.currentTimeMillis(), propertyPlaceholder
				.getPricePerOrderUSD(), lastPriceInUSDT,
				propertyPlaceholder.getTradeBuyFee(), propertyPlaceholder.getTradeSellFee(), lastPriceBTC,
				OrderBuyReason.STOPLOSS_PROTECTION_REBUY.getCST());
		newOrder.setAmount(quantity);
		newOrder.setActive(true);
		newOrder.setOpen(true);
		newOrder.setStopLossPriceValue(openOrder.getSellPriceBTCForUnit());
		newOrder.setParentId(openOrder.getId());

		Order newOrderWithId = orderService.create(newOrder);

		MacdIndicator oldMacdIndicator = macdIndicatorService.getMacdIndicatorByOrderId(openOrder.getId());

		MacdIndicator macdIndicatorForNewOrder = MacdIndicator.createNewInstance(oldMacdIndicator, newOrderWithId
				.getId(), newOrderWithId.getBuyTime(), tradingData);

		log.debug(newOrder.toString());
		log.debug(macdIndicatorForNewOrder.toString());

		log.debug("RE-BUY - close parent order id: " + openOrder.getId() + " symbol: " + openOrder.getSymbol());
		openOrder.setOpen(false);
		orderService.update(openOrder);

		macdIndicatorService.create(macdIndicatorForNewOrder);
	}

	private ArrayList<Float> getCandlesticksValues(String symbol, String candlestickPeriod, int candlecticksCount,
												   boolean removeLastOpenCandlestick) {
		List<Candlestick> candlesticks = binanceApiServiceMKA.getCandlestickBars(symbol, candlestickPeriod,
				candlecticksCount);

		if (removeLastOpenCandlestick) {
			candlesticks.remove(candlesticks.size() - 1);
		}

		ArrayList<Float> lastPrices = new ArrayList<>();

		for (Candlestick candlestick : candlesticks) {
			lastPrices.add(Float.parseFloat(candlestick.getClose()));
		}

		return lastPrices;
	}

	protected double makeServerBuyOrder(String symbol, double lastPriceBTC) throws BinanceApiException {

		NewOrder newOrderBuy = null;
		NewOrderResponse newOrderBuyResponse = null;
		double quantity;

		try {

			quantity = MathUtil.cutDecimalsWithoutRound(propertyPlaceholder.getPricePerOrderBTC() / lastPriceBTC,
					CommonUtil.getNumberOfDecimalPlacesToOrder(symbol, CoreEngine.EXCHANGE_INFO_CONTEXT.getSymbols()));
			newOrderBuy = NewOrder.marketBuy(symbol, Double.toString(quantity));
			newOrderBuy = newOrderBuy.timestamp(DateTimeUtils.getTimeStamp());

			log.debug(newOrderBuy);
			log.debug("timestamp: " + newOrderBuy.getTimestamp() + " date : " + new Date(newOrderBuy.getTimestamp()));
			log.debug("rcvdWindow: " + newOrderBuy.getRecvWindow());
			//log.debug("serverTime:" + new Date(binanceApiServiceMKA.getServerTime()));

			newOrderBuyResponse = binanceApiServiceMKA.newOrder(newOrderBuy);
			log.info("========== SUCESSFULL BOUGHT COIN PROD ENVIROMENT ==========");
		} catch (BinanceApiException e) {
			log.error("========== FATAL ERROR WHEN TRY TO BUY COIN ==========");
			log.error("========== OrderBuy > " + newOrderBuy != null ? newOrderBuy : "");
			log.error("========== OrderBuyResponse > " + newOrderBuyResponse != null ?
					newOrderBuyResponse : "");
			log.error("========== BinanceApiException > " + e.getMessage());
			log.error("========== FATAL ERROR WHEN TRY TO BUY COIN ==========");
			throw new OrderApiException("Order BUY symbol " + symbol + " FAILED " + e.getMessage());
		}
		return quantity;
	}

	protected void makeServerSellOrder(Order order) throws BinanceApiException {

		NewOrder orderSell = null;
		NewOrderResponse orderSellResponse = null;

		try {
			//log.debug("localTime" + new Date(DateTimeUtils.getTimeStamp()));
			//log.debug("serverTime:" + new Date(binanceApiServiceMKA.getServerTime()));
			Account acount = binanceApiServiceMKA.getAccount(BinanceApiConstants.DEFAULT_RECEIVING_WINDOW,
					DateTimeUtils.getTimeStamp());

			String quantity = Double.toString(order.getAmount());

			//if 0 quantity sell all
			if (StringUtils.isBlank(quantity) || quantity.equals("0") || quantity.equals("0.0")) {
				AssetBalance assetBalance = acount.getAssetBalance(order.getSymbol().substring(0, order.getSymbol()
						.length() - 3));
				quantity = Double.toString(MathUtil.cutDecimalsWithoutRound(Double.parseDouble(assetBalance.getFree()),
						CommonUtil.getNumberOfDecimalPlacesToOrder(order.getSymbol(), CoreEngine.EXCHANGE_INFO_CONTEXT
								.getSymbols())));
			}

			orderSell = NewOrder.marketSell(order.getSymbol(), quantity);
			orderSell.timestamp(DateTimeUtils.getTimeStamp());

			log.debug(orderSell);
			log.debug("timestamp: " + orderSell.getTimestamp() + " date : " + new Date(orderSell.getTimestamp()));
			log.debug("rcvdWindow: " + orderSell.getRecvWindow());
			//log.debug("serverTime:" + new Date(binanceApiServiceMKA.getServerTime()));

			orderSellResponse = binanceApiServiceMKA.newOrder(orderSell);

			log.info("========== SUCESSFULL SOLD COIN PROD ENVIROMENT ==========");
			log.info("==========" + orderSellResponse + "==========");
		} catch (BinanceApiException e) {
			log.error("========== FATAL ERROR WHEN TRY TO SELL COIN ==========");
			log.error("========== OrderSell > " + orderSell);
			log.error("========== OrderSellResponse > " + orderSellResponse);
			log.error("========== BinanceApiException > " + e.getMessage());
			log.error("========== FATAL ERROR WHEN TRY TO SELL COIN ==========");
			throw new OrderApiException("Order SELL symbol " + order.getSymbol() + " FAILED " + e.getMessage());
		}
	}
}
