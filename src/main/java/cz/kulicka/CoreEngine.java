package cz.kulicka;

import com.binance.api.client.domain.general.ExchangeInfo;
import cz.kulicka.constant.CurrenciesConstants;
import cz.kulicka.entity.Order;
import cz.kulicka.entity.Ticker;
import cz.kulicka.enums.StrategyEnum;
import cz.kulicka.service.BinanceApiServiceMKA;
import cz.kulicka.service.MacdIndicatorService;
import cz.kulicka.service.MailService;
import cz.kulicka.service.OrderService;
import cz.kulicka.strategy.OrderStrategyContext;
import cz.kulicka.strategy.impl.EMAStrategyImpl;
import cz.kulicka.util.DateTimeUtils;
import cz.kulicka.util.IOUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class CoreEngine {

	static Logger log = Logger.getLogger(CoreEngine.class);

	public static Long DATE_DIFFERENCE_BETWEN_SERVER_AND_CLIENT_MILISECONDS;
	public static ExchangeInfo EXCHANGE_INFO_CONTEXT;

	@Autowired
	OrderService orderService;
	@Autowired
	BinanceApiServiceMKA binanceApiServiceMKA;
	@Autowired
	PropertyPlaceholder propertyPlaceholder;
	@Autowired
	OrderStrategyContext orderStrategyContext;
	@Autowired
	MacdIndicatorService macdIndicatorService;

	@Autowired
	MailService mailService;

	boolean mutex = false;

	public void synchronizeServerTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		log.info("------ Date synch ------");
		log.info("Date before synch: " + dateFormat.format(new Date()));
		Date dateFromServer = new Date(EXCHANGE_INFO_CONTEXT.getServerTime());
		log.info("Server date: " + dateFormat.format(dateFromServer));
		DATE_DIFFERENCE_BETWEN_SERVER_AND_CLIENT_MILISECONDS = new Date().getTime() - (dateFromServer.getTime());
		log.info("Date after synch: " + dateFormat.format(DateTimeUtils.getCurrentServerDate()));
	}

	public void loadExchangeContext() {
		EXCHANGE_INFO_CONTEXT = binanceApiServiceMKA.getExchangeInfo();
		synchronizeServerTime();
	}

	public void setOrderStrategy(String strategy) {

		StrategyEnum strategyEnum = StrategyEnum.valueOf(strategy);

		switch (strategyEnum) {
			case MACD:
				log.info("----- ERROR MACD STRATEGY SET -----");
				break;
			case EMA:
				log.info("----- EMA STRATEGY SET -----");
				orderStrategyContext.setOrderStrategy(new EMAStrategyImpl(binanceApiServiceMKA, macdIndicatorService, orderService, propertyPlaceholder));
				break;
			default:
				log.info("----- DEFAULT EMA STRATEGY SET -----");
				orderStrategyContext.setOrderStrategy(new EMAStrategyImpl(binanceApiServiceMKA, macdIndicatorService, orderService, propertyPlaceholder));
		}
	}

	public void scanCurrenciesAndMakeNewOrders() {
		ArrayList<Ticker> newCurrencies = new ArrayList<>();
		ArrayList<Ticker> currencies;
		log.info("SCAN START!");

		currencies = binanceApiServiceMKA.checkActualCurrencies(newCurrencies);
		log.debug("Number of currencies: " + currencies.size());

		double actualBTCUSDT = Double.parseDouble(binanceApiServiceMKA.getLastPrice(CurrenciesConstants.BTCUSDT).getPrice());

		for (Ticker currencyTicker : currencies) {
			//Buy???
			if (orderStrategyContext.buy(currencyTicker, actualBTCUSDT)) {
				//TODO handle that!
				log.debug("buy now!");
			}

			if (propertyPlaceholder.isStopLossProtection()) {
				orderStrategyContext.rebuyStopLossProtection(currencyTicker, actualBTCUSDT);
			}
		}


		log.info("SCAN COMPLETE!");
	}

	public void handleActiveOrders(boolean instaSell) {
		List<Order> activeOrders = orderService.getAllActive();
		double actualBTCUSDT = Double.parseDouble(binanceApiServiceMKA.getLastPrice(CurrenciesConstants.BTCUSDT).getPrice());
		boolean endOrder;
		boolean checkProfits = false;

		log.info("Handle ACTIVE orders start > " + activeOrders.size() + " active orders");

		for (Order order : activeOrders) {
			//Sell by strategy?

			double lastPriceBTC = Double.parseDouble(binanceApiServiceMKA.getLastPrice(order.getSymbol()).getPrice());
			order.setActualPriceBTCForUnit(lastPriceBTC);

			if (instaSell) {
				endOrder = orderStrategyContext.instaSellForProfit(order, actualBTCUSDT, lastPriceBTC);
			} else {
				endOrder = orderStrategyContext.sell(order, actualBTCUSDT, lastPriceBTC);
			}

			if (endOrder) {
				log.info("Order STOPPED : " + order.toString());
				checkProfits = true;
			} else {
				if (!instaSell) {
					log.info("Continuing order: " + order.toString());
				}
			}
			//orderService.saveAll(activeOrders);
		}
		log.info("Handle ACTIVE orders finished!");

		if (checkProfits) {
			checkProfits();
		}
		reportActiveOrders();
	}

	public void handleOpenOrders() {
		List<Order> openOrders = orderService.getAllOpenButNotActive();

		log.info("Handle OPEN orders start > " + openOrders.size() + " active orders");

		for (Order order : openOrders) {
			if (orderStrategyContext.closeNonActiveOpenOrder(order)) {
				log.debug("Close order id: " + order.getId() + " symbol: " + order.getSymbol());
				order.setOpen(false);
			}
		}

		log.info("Handle OPEN orders finished!");

		orderService.saveAll(openOrders);
	}

	public void checkProfits() {
		List<Order> finishedOrders = orderService.findAllByActiveFalseAndSellPriceForOrderWithFeeIsNotNull();

		double profit = 0;

		for (Order order : finishedOrders) {
			profit += order.getProfitFeeIncluded();
		}

		log.info("=================================== FINAL PROFIT: " + String.format("%.9f", (profit)) + " $$$ ===================================");

		IOUtil.finishedOrderToCsv(new ArrayList<>(finishedOrders), propertyPlaceholder.getCsvReportFilePath(), false, propertyPlaceholder.getBlackListCoins());
	}

	public void dailyReport() {
		List<Order> finishedOrders = orderService.findAllByActiveFalse();
		List<Order> dailyOrders = new ArrayList<>();

		for (Order order : finishedOrders) {
			if (DateTimeUtils.yesterday().getTime() < order.getSellTime()) {
				dailyOrders.add(order);
			}
		}

		log.info("Report finished order per day count > " + dailyOrders.size());

		//TODO file path hotfix
		IOUtil.ordersToCSV(new ArrayList<>(finishedOrders),
				DateTimeUtils.getPathWithDate(propertyPlaceholder.getCsvReportDailyFilePath(), DateTimeUtils.yesterday()).concat(".csv"), false);
	}

	public void reportActiveOrders() {
		List<Order> openOrders = orderService.getAllActive();

		log.info("Report active orders count > " + openOrders.size());

		IOUtil.activeOrdersToCSV(new ArrayList<>(openOrders), propertyPlaceholder.getCsvReportOpenOrdersFilePath(), false);
	}


	public void panicSell() {
		if (propertyPlaceholder.isCoinMachineOn()) {
			orderStrategyContext.panicSellAll();
		}
	}

	public boolean isMutex() {
		return mutex;
	}

	public void setMutex(boolean mutex) {
		this.mutex = mutex;
	}

}
