package cz.kulicka;

import cz.kulicka.services.BinanceApiService;
import cz.kulicka.strategy.OrderStrategyContext;
import cz.kulicka.strategy.impl.SecondDumbStrategyImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.swing.*;

@SpringBootApplication
public class Main implements CommandLineRunner {
    //https://github.com/binance-exchange/binance-official-api-docs/blob/master/rest-api.md
    //mvn spring-boot:run
    @Autowired
    CoreEngine coreEngine;

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        coreEngine.runIt();
    }
}
