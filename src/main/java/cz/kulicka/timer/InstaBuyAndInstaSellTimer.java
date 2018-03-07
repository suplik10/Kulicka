package cz.kulicka.timer;

import cz.kulicka.CoreEngine;
import cz.kulicka.exception.BinanceApiException;
import org.apache.log4j.Logger;

import java.util.Calendar;
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


            Calendar calendar = Calendar.getInstance();
            int minutes = calendar.get(Calendar.MINUTE);

            if(minutes > 8 && minutes < 55){
                log.info("------ TIMER STARTS INSTA BUY&SELL ------");
                coreEngine.scanCurrenciesAndMakeNewOrders();
                coreEngine.handleActiveOrders(true);
            }
        } catch (BinanceApiException e) {
            log.error("BINANCE API EXCEPTION !!!  " + e.getMessage());
        }
    }
}