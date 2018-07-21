package cz.kulicka;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.impl.BinanceApiRestClientImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KulickaConfiguration {

	@Autowired
	PropertyPlaceholder propertyPlaceholder;

	@Bean
	public BinanceApiRestClient binanceApiRestClient() {
		return new BinanceApiRestClientImpl(propertyPlaceholder.getApiKey(), propertyPlaceholder.getSecret());
	}
}
