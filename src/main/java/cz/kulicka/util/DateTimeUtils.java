package cz.kulicka.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateTimeUtils {

	public static long getTimeStamp() {
		return System.currentTimeMillis() - 5000;
	}

	public static int convertRequestPeriodToMin(String candlestickPeriod) {
		switch (candlestickPeriod) {
			case "5m":
				return 5;
			case "15m":
				return 15;
			case "30m":
				return 30;
			case "1h":
				return 60;
			case "2h":
				return 120;
			case "4h":
				return 240;
			case "6h":
				return 360;
			case "8h":
				return 480;
			case "12h":
				return 720;
			case "1d":
				return 1440;
			default:
				throw new IllegalArgumentException("Invalid candle period!");
		}
	}

	public static Calendar roundCalendarToMinutes(int minutes) {
		Calendar roundedCalendar = new GregorianCalendar();
		roundedCalendar.setTime(new Date());

		int minute = roundedCalendar.get(Calendar.MINUTE);
		minute = minute % minutes;
		if (minute != 0) {
			int minuteToAdd = minutes - minute;
			roundedCalendar.add(Calendar.MINUTE, minuteToAdd);
		} else {
			roundedCalendar.add(Calendar.MINUTE, minutes);
		}
		roundedCalendar.set(Calendar.SECOND, 0);

		return roundedCalendar;
	}

	public static Date yesterday() {
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		return cal.getTime();
	}

	public static boolean isTimeToDailyReport() {
		Calendar calendar = Calendar.getInstance();

		if (calendar.get(Calendar.HOUR) == 0) {
			return true;
		}
		return false;
	}

	public static String getPathWithDate(String path, Date date) {
		DateFormat df = new SimpleDateFormat("MM_dd_yyyy");
		return path.concat(df.format(date));
	}
}
