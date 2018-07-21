package cz.kulicka;

import cz.kulicka.service.BinanceApiServiceMKA;
import cz.kulicka.service.MacdIndicatorService;
import cz.kulicka.service.MailService;
import cz.kulicka.service.OrderService;
import cz.kulicka.timer.InstaBuyAndInstaSellTimer;
import cz.kulicka.timer.SellTimer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import static cz.kulicka.util.DateTimeUtils.convertRequestPeriodToMin;
import static cz.kulicka.util.DateTimeUtils.roundCalendarToMinutes;

@Component
public class ExchangeCommandCenter {

	static Logger log = LogManager.getLogger(ExchangeCommandCenter.class);

	@Autowired
	CoreEngine coreEngine;
	@Autowired
	PropertyPlaceholder propertyPlaceholder;
	@Autowired
	OrderService orderService;
	@Autowired
	BinanceApiServiceMKA binanceApiServiceMKA;
	@Autowired
	MacdIndicatorService macdIndicatorService;
	@Autowired
	MailService mailService;

	public void runIt() {
		coreEngine.loadExchangeContext();
		coreEngine.synchronizeServerTime();
		coreEngine.setOrderStrategy(propertyPlaceholder.getActualStrategy());

		//Insta BUY&SELL Timer
		Date newDateForInstaSellInstaBuyTimer = new Date(roundCalendarToMinutes(propertyPlaceholder.getTimeDifferenceBetweenRequestsInMinutes()).getTimeInMillis()
				- CoreEngine.DATE_DIFFERENCE_BETWEN_SERVER_AND_CLIENT_MILISECONDS + 10000);

		log.info("NewDateForInstaSellInstaBuyTimer : " + newDateForInstaSellInstaBuyTimer);

		Timer instaSellInstaBuyTimer = new Timer();
		instaSellInstaBuyTimer.schedule(new InstaBuyAndInstaSellTimer(coreEngine, propertyPlaceholder, mailService)
				, newDateForInstaSellInstaBuyTimer, TimeUnit.MINUTES.toMillis(propertyPlaceholder.getTimeDifferenceBetweenRequestsInMinutes()));

		//Sell Timer
		Calendar calendar = roundCalendarToMinutes(convertRequestPeriodToMin(propertyPlaceholder.getBinanceCandlesticksPeriod()));
		calendar.add(Calendar.MINUTE, 1);
		Date newDateForSellTimer = new Date(calendar.getTimeInMillis());

		log.info("NewDateForSellTimer : " + newDateForSellTimer);

		Timer sellTimer = new Timer();
		sellTimer.schedule(new SellTimer(coreEngine, mailService), newDateForSellTimer,
				TimeUnit.MINUTES.toMillis(convertRequestPeriodToMin(propertyPlaceholder.getBinanceCandlesticksPeriod())));

	}
}
