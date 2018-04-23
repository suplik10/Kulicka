package cz.kulicka;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;

public class UIController extends JFrame {

    static Logger log = LogManager.getLogger(UIController.class);

    private JButton panicSellButton;
    private JButton makeNewOrderButton;

    JPanel mainframe = new JPanel();

    private CoreEngine coreEngine;

    public UIController(final String title) {
        super(title);
        setPanicButton();
        this.getContentPane().add(mainframe);
        this.pack();
    }

    public void setCoreEngine(CoreEngine coreEngine) {
        this.coreEngine = coreEngine;
        this.setTitle(coreEngine.propertyPlaceholder.getAppName());
        setMakeNewOrdersButton();
    }

    public void setPanicButton() {
        panicSellButton = new JButton("OMG PANIC BUTTONEK!!!");
        panicSellButton.setBackground(Color.RED);
        mainframe.add(panicSellButton);
        panicSellButton.addActionListener(new Action() {
            @Override
            public Object getValue(String key) {
                return null;
            }

            @Override
            public void putValue(String key, Object value) {

            }

            @Override
            public void setEnabled(boolean b) {

            }

            @Override
            public boolean isEnabled() {
                return false;
            }

            @Override
            public void addPropertyChangeListener(PropertyChangeListener listener) {

            }

            @Override
            public void removePropertyChangeListener(PropertyChangeListener listener) {

            }

            @Override
            public void actionPerformed(ActionEvent e) {
                coreEngine.setMutex(true);
                coreEngine.panicSell();
                panicSellButton.setEnabled(false);
                panicSellButton.setText("Closing active orders... for more info check log...");

                log.info("PANNIC SELL CLOSE!");

                System.exit(0);
            }
        });

    }

    public void setMakeNewOrdersButton() {
        makeNewOrderButton = new JButton("Make new orders " + coreEngine.propertyPlaceholder.isAllowNewOrders());
        mainframe.add(makeNewOrderButton);
        this.pack();
        makeNewOrderButton.addActionListener(new Action() {
            @Override
            public Object getValue(String key) {
                return null;
            }

            @Override
            public void putValue(String key, Object value) {

            }

            @Override
            public void setEnabled(boolean b) {

            }

            @Override
            public boolean isEnabled() {
                return false;
            }

            @Override
            public void addPropertyChangeListener(PropertyChangeListener listener) {

            }

            @Override
            public void removePropertyChangeListener(PropertyChangeListener listener) {

            }

            @Override
            public void actionPerformed(ActionEvent e) {
                coreEngine.propertyPlaceholder.setAllowNewOrders(!coreEngine.propertyPlaceholder.isAllowNewOrders());
                makeNewOrderButton.setBackground(Color.RED);
                makeNewOrderButton.setText("Make new orders " + coreEngine.propertyPlaceholder.isAllowNewOrders());
            }
        });
    }
}
