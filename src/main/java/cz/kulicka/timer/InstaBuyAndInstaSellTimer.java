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
			log.error("BINANCE API EXCEPTION !!!  " + e.toString());
			try {
				mailService.sendMail("BINANCE API EXCEPTION: ".concat(e.toString()), new Date(DateTimeUtils.getCurrentServerTimeStamp()));
			} catch (Exception eMail) {
				log.error("EXCEPTION SENDING MAIL SOOO FUNNY !!!!  " + eMail.toString());
			}
		} catch (Exception e) {
			try {
				log.error("PROGRAM THREAD EXCEPTION !!!  " + e.toString());
				mailService.sendMail("PROGRAM THREAD EXCEPTION: ".concat(e.toString()), new Date(DateTimeUtils.getCurrentServerTimeStamp()));
			} catch (Exception eMail) {
				log.error("EXCEPTION SENDING MAIL SOOO FUNNY !!!!  " + eMail.toString());
			}
		} finally {
			coreEngine.setMutex(false);
		}
	}
}