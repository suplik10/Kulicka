package cz.kulicka.timer;

import cz.kulicka.CoreEngine;
import cz.kulicka.PropertyPlaceholder;
import cz.kulicka.exception.BinanceApiException;
import org.apache.log4j.Logger;

import java.util.TimerTask;

public class InstaBuyAndInstaSellTimer extends TimerTask {

    static Logger log = Logger.getLogger(InstaBuyAndInstaSellTimer.class);

    private CoreEngine coreEngine;
    private PropertyPlaceholder propertyPlaceholder;
    private int iteration = 0;

    public InstaBuyAndInstaSellTimer(CoreEngine coreEngine, PropertyPlaceholder propertyPlaceholder) {
        super();
        this.coreEngine = coreEngine;
        this.propertyPlaceholder = propertyPlaceholder;
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

            if(propertyPlaceholder.isAllowNewOrders()){
                coreEngine.scanCurrenciesAndMakeNewOrders();
            }else{
                log.warn("------ MAKING OF NEW ORDERS IS DISABLED ------");
            }

            coreEngine.handleActiveOrders(true);

            if (propertyPlaceholder.isStopLossProtection()) {
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