package cz.kulicka.timer;

import cz.kulicka.CoreEngine;
import cz.kulicka.exception.BinanceApiException;
import org.apache.log4j.Logger;

import java.util.TimerTask;

public class SellTimer extends TimerTask {

    static Logger log = Logger.getLogger(SellTimer.class);

    private CoreEngine coreEngine;
    private int iteration = 0;

    public SellTimer(CoreEngine coreEngine) {
        super();
        this.coreEngine = coreEngine;
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
            log.info("------ TIMER SELL - START " + iteration + " ------");
            coreEngine.handleActiveOrders(false);
            log.info("------ TIMER SELL - END " + iteration + " ------");
        } catch (BinanceApiException e) {
            log.error("BINANCE API EXCEPTION !!! iteration: " + iteration + " exception: " + e.getMessage());
        } finally {
            coreEngine.setMutex(false);
        }
    }
}
