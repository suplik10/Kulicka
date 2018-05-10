package cz.kulicka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.swing.*;
import java.awt.*;

//kulicka.bot@gmail.com Heslo0001!

@SpringBootApplication
public class Main extends JFrame implements CommandLineRunner {
    //https://github.com/binance-exchange/binance-official-api-docs/blob/master/rest-api.md
    //mvn spring-boot:run
    @Autowired
    ExchangeCommandCenter exchangeCommandCenter;

    @Autowired
    CoreEngine coreEngine;

    UIController uiController;

    public Main() throws HeadlessException {
        super();
        super.setTitle("hm");
        uiController = new UIController("Exchnage UI");
        uiController.pack();
        uiController.setSize(500, 200);
        uiController.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        uiController.setVisible(true);
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        uiController.setCoreEngine(coreEngine);
        exchangeCommandCenter.runIt();
    }
}
