package cz.kulicka;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class ThreadDemo extends Thread {

        private String threadName;

        ThreadDemo( String name) {
            threadName = name;
            System.out.println("Creating " +  threadName );
        }

        public void run() {
            System.out.println("Running " +  threadName );
            try {
                for(int i = 4; i > 0; i--) {
                    System.out.println("Thread: " + threadName + ", " + i);
                    // Let the thread sleep for a while.
                    Thread.sleep(50);
                }
            }catch (InterruptedException e) {
                System.out.println("Thread " +  threadName + " interrupted.");
            }
            System.out.println("Thread " +  threadName + " exiting.");
        }
}

class TestThread {
    public static void main(String args[]) {

        List<ThreadDemo> listOfThreads = new ArrayList<>();
        boolean threadsRunning;

        for (int i = 0; i < 100; i++){
            ThreadDemo T1 = new ThreadDemo( "Thread-" + i);
            T1.start();
            listOfThreads.add(T1);
        }

        threadsRunning= true;

        while(threadsRunning){
            threadsRunning = false;
            for (ThreadDemo thread : listOfThreads){
                if(thread.isAlive()){
                    threadsRunning = true;
                    break;
                }
            }

        }

    }


}


