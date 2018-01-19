package cz.kulicka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PropertyPlacehoder {

    @Value("${app.thread.sleep.between.requests.miliseconds}")
    private int threadSleepBetweenRequestsMiliseconds;

    public int getThreadSleepBetweenRequestsMiliseconds() {
        return threadSleepBetweenRequestsMiliseconds;
    }

}
