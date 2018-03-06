package cz.kulicka.timer;

import cz.kulicka.CoreEngine;
import cz.kulicka.exception.BinanceApiException;
import org.apache.log4j.Logger;

import java.util.TimerTask;

public class InstaBuyAndInstaSellTimer extends TimerTask {

    static Logger log = Logger.getLogger(InstaBuyAndInstaSellTimer.class);

    private CoreEngine coreEngine;

    public InstaBuyAndInstaSellTimer(CoreEngine coreEngine) {
        super();
        this.coreEngine = coreEngine;
    }

    @Override
    public void run() {
        try {
            log.info("------ TIMER STARTS INSTA BUY&SELL ------");
            //if(!coreEngine.isTimerLock()){
              //  log.info("------ INSTA BUY&SELL LOCKED THREAD------");
                coreEngine.setTimerLock(true);
                coreEngine.scanCurrenciesAndMakeNewOrders();
                coreEngine.handleActiveOrders(true);
                coreEngine.setTimerLock(false);
                //log.info("------ INSTA BUY&SELL UNLOCKED THREAD------");
            //}
        } catch (BinanceApiException e) {
            log.error("BINANCE API EXCEPTION !!!  " + e.getMessage());
        }
    }
}