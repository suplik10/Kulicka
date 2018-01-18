package cz.kulicka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


//@ImportResource("classpath:spring-sftp-config.xml")
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
        coreEngine.run();
    }
}
