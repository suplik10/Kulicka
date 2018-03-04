package cz.kulicka;

import cz.kulicka.service.MacdIndicatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.awt.*;

@SpringBootApplication
public class Main implements CommandLineRunner  {
    //https://github.com/binance-exchange/binance-official-api-docs/blob/master/rest-api.md
    //mvn spring-boot:run
    @Autowired
    CoreEngine coreEngine;

    @Autowired
    MacdIndicatorService macdIndicatorService;

    public Main() throws HeadlessException {
        super();
           }

    public void init() {

        final BarChartJframe demo = new BarChartJframe("XY Series Demo 3", macdIndicatorService);
        demo.pack();
        //RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);


    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);

    }


    @Override
    public void run(String... strings) throws Exception {
        coreEngine.runIt();
    }
}
