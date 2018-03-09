package cz.kulicka.timer;

import cz.kulicka.CoreEngine;
import cz.kulicka.exception.BinanceApiException;
import org.apache.log4j.Logger;

import java.util.TimerTask;

public class SellTimer extends TimerTask {

    static Logger log = Logger.getLogger(SellTimer.class);

    private CoreEngine coreEngine;
    private int iteration = 0;
    private int candlestickPeriod;

    public SellTimer(CoreEngine coreEngine, int candlestickPeriod) {
        super();
        this.coreEngine = coreEngine;
        this.candlestickPeriod = candlestickPeriod;
    }

    @Override
    public void run() {
        try {
            iteration++;
            log.info("------ TIMER SELL - START " + iteration + " ------");
            coreEngine.handleActiveOrders(false);
            log.info("------ TIMER SELL - END " + iteration + " ------");
        } catch (BinanceApiException e) {
            log.error("BINANCE API EXCEPTION !!! iteration: " + iteration + " exception: " + e.getMessage());
        }
    }
}
