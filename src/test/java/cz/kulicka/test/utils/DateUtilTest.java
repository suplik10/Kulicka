package cz.kulicka.test.utils;

import cz.kulicka.CoreEngine;
import cz.kulicka.timer.InstaBuyAndInstaSellTimer;
import cz.kulicka.timer.SellTimer;
import cz.kulicka.util.DateTimeUtils;
import org.junit.Test;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class DateUtilTest {

    @Test
    public void getStartDateTest() throws IOException {

        //5m,15m,30m,1h,2h,4h,6h,8h,12h,1d

        String candlePeriod = "1m";

        Calendar roundedCalendar = new GregorianCalendar();
        roundedCalendar.setTime(new Date());

        Date newDate;

        switch (candlePeriod) {
            case "1m":
                roundedCalendar = roundCalendarToMinutes(1, roundedCalendar);
                newDate = new Date(roundedCalendar.getTimeInMillis());
                break;
            case "5m":
                roundedCalendar = roundCalendarToMinutes(5, roundedCalendar);
                newDate = new Date(roundedCalendar.getTimeInMillis());
                break;
            case "15m":
                roundedCalendar = roundCalendarToMinutes(15, roundedCalendar);
                newDate = new Date(roundedCalendar.getTimeInMillis());
                break;
            case "30m":
                roundedCalendar = roundCalendarToMinutes(30, roundedCalendar);
                newDate = new Date(roundedCalendar.getTimeInMillis());
                break;
            case "1h":
                roundedCalendar = roundCalendarToMinutes(60, roundedCalendar);
                newDate = new Date(roundedCalendar.getTimeInMillis());
                break;
            case "2h":
                roundedCalendar = roundCalendarToMinutes(120, roundedCalendar);
                newDate = new Date(roundedCalendar.getTimeInMillis());
                break;
            case "4h":
                roundedCalendar = roundCalendarToMinutes(240, roundedCalendar);
                newDate = new Date(roundedCalendar.getTimeInMillis());
                break;
            case "6h":
                roundedCalendar = roundCalendarToMinutes(360, roundedCalendar);
                newDate = new Date(roundedCalendar.getTimeInMillis());
                break;
            case "8h":
                roundedCalendar = roundCalendarToMinutes(480, roundedCalendar);
                newDate = new Date(roundedCalendar.getTimeInMillis());
                break;
            case "12h":
                roundedCalendar = roundCalendarToMinutes(720, roundedCalendar);
                newDate = new Date(roundedCalendar.getTimeInMillis());
                break;
            case "1d":
                roundedCalendar = roundCalendarToMinutes(1440, roundedCalendar);
                newDate = new Date(roundedCalendar.getTimeInMillis());
                break;
            default:
                throw new IllegalArgumentException("Invalid candle period!");
        }


        roundedCalendar.setTime(new Date());
        roundedCalendar.add(Calendar.HOUR, 1);
        roundedCalendar.set(Calendar.MINUTE, 0);
        roundedCalendar.set(Calendar.SECOND, 0);
        Date roundOneHourDate = new Date(roundedCalendar.getTimeInMillis());

        Calendar actualDate = Calendar.getInstance();
        actualDate.setTime(new Date());

        new Date().getMinutes();
        int dayOfWeek = actualDate.get(Calendar.DAY_OF_WEEK);


        Calendar calendar = Calendar.getInstance();
        calendar.set(
                Calendar.DAY_OF_WEEK,
                actualDate.get(Calendar.DAY_OF_WEEK)
        );
        calendar.set(Calendar.HOUR_OF_DAY, actualDate.get(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, actualDate.get(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, actualDate.get(Calendar.SECOND));

        //Assert.assertTrue(saveListOfStringsToFile(testArray, "src/test/resources/IOTestFile"));
    }

    private Calendar roundCalendarToMinutes(int minutes, Calendar roundedCalendar) {
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

    @Test
    public void commonTest() {
        Calendar calendar = Calendar.getInstance();
        int candlestickPeriod = 5;

        int actualMinutes = calendar.get(Calendar.MINUTE);

        calendar.set(Calendar.MINUTE, candlestickPeriod + 5);

        int downBorderMin = calendar.get(Calendar.MINUTE);

        calendar.set(Calendar.MINUTE, candlestickPeriod - 5);

        int upperBorderMin = calendar.get(Calendar.MINUTE);
    }

    @Test
    public void taskTest() {
        Timer instaBuyTimer = new Timer();

        instaBuyTimer.schedule(new InstaBuyAndInstaSellTimer(null, null)
                , new Date(), 1000);


        Timer sellTimer = new Timer();
        sellTimer.schedule(new SellTimer(null), new Date(),
                3000);


        BlockingQueue<Timer> blockingQueue = new LinkedBlockingDeque<>();

        blockingQueue.add(instaBuyTimer);
        blockingQueue.add(sellTimer);
    }

    @Test
    public void getYesterdayTest() {

        Date dadf = new Date(1524328412946L);

        Date kurva = new Date(new Date().getTime() - 58000);

        String safd = DateTimeUtils.getPathWithDate("patfff", DateTimeUtils.yesterday());

        Date date = DateTimeUtils.yesterday();
        String pathWithDate = "reprot_";

        DateFormat df = new SimpleDateFormat("MM_dd_yyyy");
        pathWithDate = pathWithDate.concat(df.format(date));



    }

    @Test
    public void dailyReportTest() {

        boolean isTimeToDailyReport = false;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        int hour = calendar.get(Calendar.HOUR);
        if(calendar.get(Calendar.HOUR) == 0){

        }
    }

}
