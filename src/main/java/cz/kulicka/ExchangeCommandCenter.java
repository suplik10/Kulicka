package cz.kulicka;

import cz.kulicka.service.BinanceApiService;
import cz.kulicka.service.MacdIndicatorService;
import cz.kulicka.service.OrderService;
import cz.kulicka.strategy.impl.MacdStrategyImpl;
import cz.kulicka.timer.InstaBuyAndInstaSellTimer;
import cz.kulicka.timer.SellTimer;
import org.apache.log4j.Logger;
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

    static Logger log = Logger.getLogger(ExchangeCommandCenter.class);

    @Autowired
    CoreEngine coreEngine;
    @Autowired
    PropertyPlaceholder propertyPlaceholder;
    @Autowired
    OrderService orderService;
    @Autowired
    BinanceApiService binanceApiService;
    @Autowired
    MacdIndicatorService macdIndicatorService;

    public void runIt() {

        coreEngine.synchronizeServerTime();
        coreEngine.setOrderStrategy(new MacdStrategyImpl(binanceApiService, macdIndicatorService, orderService, propertyPlaceholder));

        //Insta BUY&SELL Timer
        Date newDateForInstaSellInstaBuyTimer = new Date(roundCalendarToMinutes(propertyPlaceholder.getTimeDifferenceBetweenRequestsInMinutes()).getTimeInMillis()
                - CoreEngine.DATE_DIFFERENCE_BETWEN_SERVER_AND_CLIENT_MILISECONDS + 10000);

        log.info("newDateForInstaSellInstaBuyTimer : " + newDateForInstaSellInstaBuyTimer);

        Timer instaBuyTimer = new Timer();
        instaBuyTimer.schedule(new InstaBuyAndInstaSellTimer(coreEngine), newDateForInstaSellInstaBuyTimer,
                TimeUnit.MINUTES.toMillis(propertyPlaceholder.getTimeDifferenceBetweenRequestsInMinutes()));

        //Sell Timer
        Calendar calendar = roundCalendarToMinutes(convertRequestPeriodToMin(propertyPlaceholder.getBinanceCandlesticksPeriod()));
        calendar.add(Calendar.MINUTE, 1);
        Date newDateForSellTimer = new Date(calendar.getTimeInMillis());

        log.info("newDateForSellTimer : " + newDateForSellTimer);

        Timer sellTimer = new Timer();
        sellTimer.schedule(new SellTimer(coreEngine), newDateForSellTimer,
                TimeUnit.MINUTES.toMillis(convertRequestPeriodToMin(propertyPlaceholder.getBinanceCandlesticksPeriod())));

    }
}
