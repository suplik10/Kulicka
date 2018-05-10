package cz.kulicka.timer;

import cz.kulicka.CoreEngine;
import cz.kulicka.PropertyPlaceholder;
import cz.kulicka.exception.BinanceApiException;
import cz.kulicka.service.MailService;
import cz.kulicka.util.DateTimeUtils;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.TimerTask;

public class InstaBuyAndInstaSellTimer extends TimerTask {

	static Logger log = Logger.getLogger(InstaBuyAndInstaSellTimer.class);

	private CoreEngine coreEngine;
	private PropertyPlaceholder propertyPlaceholder;
	private MailService mailService;
	private int iteration = 0;

	public InstaBuyAndInstaSellTimer(CoreEngine coreEngine, PropertyPlaceholder propertyPlaceholder, MailService mailService) {
		super();
		this.coreEngine = coreEngine;
		this.propertyPlaceholder = propertyPlaceholder;
		this.mailService = mailService;
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

			if (propertyPlaceholder.isAllowNewOrders()) {
				coreEngine.scanCurrenciesAndMakeNewOrders();
			} else {
				log.warn("------ MAKING OF NEW ORDERS IS DISABLED ------");
			}

			coreEngine.handleActiveOrders(true);

			if (propertyPlaceholder.isStopLossProtection()) {
				coreEngine.handleOpenOrders();
			}

			log.info("------ TIMER INSTA BUY&SELL - END " + iteration + " ------");
		} catch (BinanceApiException e) {
			log.error("BINANCE API EXCEPTION !!!  " + e.getMessage());
			mailService.sendMail(e.getMessage(), new Date(DateTimeUtils.getCurrentServerTimeStamp()));
		} finally {
			coreEngine.setMutex(false);
		}
	}
}