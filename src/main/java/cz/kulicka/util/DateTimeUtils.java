package cz.kulicka.util;

import cz.kulicka.CoreEngine;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateTimeUtils {

    public static Date getCurrentServerDate() {
        return new Date(new Date().getTime() - CoreEngine.DATE_DIFFERENCE_BETWEN_SERVER_AND_CLIENT_MILISECONDS);
    }

    public static int convertRequestPeriodToMin(String candlestickPeriod){
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


}
