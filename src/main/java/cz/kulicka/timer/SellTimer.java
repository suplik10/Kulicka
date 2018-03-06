package cz.kulicka.timer;

import cz.kulicka.CoreEngine;
import cz.kulicka.exception.BinanceApiException;
import org.apache.log4j.Logger;

import java.util.TimerTask;

public class SellTimer extends TimerTask {

    static Logger log = Logger.getLogger(SellTimer.class);

    private CoreEngine coreEngine;

    public SellTimer(CoreEngine coreEngine) {
        super();
        this.coreEngine = coreEngine;
    }

    @Override
    public void run() {
        try {
            log.info("------ TIMER STARTS SELL TIMER ------");
           // if(!coreEngine.isTimerLock()){
             //   log.info("------ SELL TIMER LOCKED THREAD------");
             //   coreEngine.setTimerLock(true);
                //IN THIS STEP VALIDATE ALL BOUGHT ORDERS BY INSTA BUY
                coreEngine.handleActiveOrders(false);
              //  coreEngine.setTimerLock(false);
               // log.info("------ SELL TIMER UNLOCKED THREAD------");
           // }
        } catch (BinanceApiException e) {
            log.error("BINANCE API EXCEPTION !!!  " + e.getMessage());
        }
    }
}
