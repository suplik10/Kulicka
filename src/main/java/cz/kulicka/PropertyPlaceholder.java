package cz.kulicka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PropertyPlaceholder {

    @Value("${app.thread.sleep.between.requests.miliseconds}")
    private int threadSleepBetweenRequestsMiliseconds;

    @Value("${app.binance.api.key}")
    private String apiKey;

    @Value("${app.binance.secret.key}")
    private String secret;

    public int getThreadSleepBetweenRequestsMiliseconds() {
        return threadSleepBetweenRequestsMiliseconds;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getSecret() {
        return secret;
    }
}
