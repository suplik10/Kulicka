package cz.kulicka.timer;

import cz.kulicka.CoreEngine;
import cz.kulicka.exception.BinanceApiException;
import org.apache.log4j.Logger;

import java.util.TimerTask;

public class InstaBuyAndInstaSellTimer extends TimerTask {

    static Logger log = Logger.getLogger(InstaBuyAndInstaSellTimer.class);

    private CoreEngine coreEngine;
    private int iteration = 0;
    private int candlestickPeriod;
    private boolean stopLossProtection;

    public InstaBuyAndInstaSellTimer(CoreEngine coreEngine, int candlestickPeriod, boolean stopLossProtection) {
        super();
        this.coreEngine = coreEngine;
        this.candlestickPeriod = candlestickPeriod;
        this.stopLossProtection = stopLossProtection;
    }

    @Override
    public void run() {

        while (coreEngine.isMutex()) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        coreEngine.setMutex(true);

        try {
            iteration++;
            log.info("------ TIMER INSTA BUY&SELL - START " + iteration + " ------");
            coreEngine.scanCurrenciesAndMakeNewOrders();
            coreEngine.handleActiveOrders(true);

            if (stopLossProtection) {
                coreEngine.handleOpenOrders();
            }

            log.info("------ TIMER INSTA BUY&SELL - END " + iteration + " ------");
        } catch (BinanceApiException e) {
            log.error("BINANCE API EXCEPTION !!!  " + e.getMessage());
        } finally {
            coreEngine.setMutex(false);
        }
    }
}