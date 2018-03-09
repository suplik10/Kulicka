package cz.kulicka.timer;

import cz.kulicka.CoreEngine;
import cz.kulicka.exception.BinanceApiException;
import org.apache.log4j.Logger;

import java.util.Calendar;
import java.util.TimerTask;

public class InstaBuyAndInstaSellTimer extends TimerTask {

    static Logger log = Logger.getLogger(InstaBuyAndInstaSellTimer.class);

    private CoreEngine coreEngine;
    private int iteration = 0;
    private int candlestickPeriod;

    public InstaBuyAndInstaSellTimer(CoreEngine coreEngine, int candlestickPeriod) {
        super();
        this.coreEngine = coreEngine;
        this.candlestickPeriod = candlestickPeriod;
    }

    @Override
    public void run() {
        try {
            Calendar calendar = Calendar.getInstance();
            int minutes = calendar.get(Calendar.MINUTE);

            if(8 < minutes && minutes < 55){
                iteration++;
                log.info("------ TIMER INSTA BUY&SELL - START " + iteration + " ------");
                coreEngine.scanCurrenciesAndMakeNewOrders();
                coreEngine.handleActiveOrders(true);
                log.info("------ TIMER INSTA BUY&SELL - END " + iteration + " ------");
            }

        } catch (BinanceApiException e) {
            log.error("BINANCE API EXCEPTION !!!  " + e.getMessage());
        }
    }
}