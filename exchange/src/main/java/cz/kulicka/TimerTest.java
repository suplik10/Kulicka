package cz.kulicka;

import org.springframework.core.ResolvableType;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

class TimerTest extends TimerTask {

    public TimerTest(){

        //Constructor

    }

    public void run() {
        try {
            System.out.println("erun threadddd " + TimeUnit.SECONDS.toMillis(5) );
            for(int i = 0; i < 10; i++){
                int v = 1111;
            }

        } catch (Exception ex) {

            System.out.println("error running thread " + ex.getMessage());
        }
    }
}
class testTask{


    public static void main(String args[]) {

        Calendar actualDate = Calendar.getInstance();
        actualDate.setTime(new Date());
        int dayOfWeek = actualDate.get(Calendar.DAY_OF_WEEK);


        Calendar calendar = Calendar.getInstance();
        calendar.set(
                Calendar.DAY_OF_WEEK,
                actualDate.get(Calendar.DAY_OF_WEEK)
        );
        calendar.set(Calendar.HOUR_OF_DAY, actualDate.get(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, actualDate.get(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, actualDate.get(Calendar.SECOND)+ 10);


        Timer time = new Timer(); // Instantiate Timer Object

        // Start running the task on Monday at 15:40:00, period is set to 8 hours
        // if you want to run the task immediately, set the 2nd parameter to 0
        time.schedule(new TimerTest(), calendar.getTime(), TimeUnit.HOURS.toMillis(5));
    }
}