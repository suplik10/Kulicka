package cz.kulicka.timer;

import cz.kulicka.CoreEngine;
import cz.kulicka.exception.BinanceApiException;
import cz.kulicka.service.MailService;
import cz.kulicka.util.DateTimeUtils;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.TimerTask;

public class SellTimer extends TimerTask {

	static Logger log = Logger.getLogger(SellTimer.class);

	private CoreEngine coreEngine;
	private MailService mailService;
	private int iteration = 0;

	public SellTimer(CoreEngine coreEngine, MailService mailService) {
		super();
		this.coreEngine = coreEngine;
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
			log.info("------ TIMER SELL - START " + iteration + " ------");
			coreEngine.handleActiveOrders(false);
			coreEngine.loadExchangeContext();

			if (DateTimeUtils.isTimeToDailyReport()) {
				coreEngine.dailyReport();
			}

			log.info("------ TIMER SELL - END " + iteration + " ------");
		} catch (BinanceApiException e) {
			log.error("BINANCE API EXCEPTION !!!  " + e.toString());
			try {
				mailService.sendMail("BINANCE API EXCEPTION: ".concat(e.toString()), new Date(DateTimeUtils.getCurrentServerTimeStamp()));
			} catch (Exception eMail) {
				log.error("EXCEPTION SENDING MAIL SOOO FUNNY !!!!  " + eMail.toString());
			}
		} catch (Exception e) {
			log.error("PROGRAM THREAD EXCEPTION !!!  " + e.toString());
			try {
				mailService.sendMail("PROGRAM THREAD EXCEPTION: ".concat(e.toString()), new Date(DateTimeUtils.getCurrentServerTimeStamp()));
			} catch (Exception eMail) {
				log.error("EXCEPTION SENDING MAIL SOOO FUNNY !!!!  " + eMail.toString());
			}
		} finally {
			coreEngine.setMutex(false);
		}
	}
}
