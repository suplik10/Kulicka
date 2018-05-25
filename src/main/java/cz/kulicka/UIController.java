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
	private JButton saveAllButton;

	private JLabel jLabel;
	private JTextField jTextField;
	private JLabel timeDifferenceBetweenRequestsInMinutesl;
	private JTextField timeDifferenceBetweenRequestsInMinutesf;
	private JLabel apiKeyl;
	private JTextField apiKeyf;
	private JLabel appNamel;
	private JTextField appNamef;
	private JLabel secretl;
	private JTextField secretf;
	private JLabel pricePerOrderBTCl;
	private JTextField pricePerOrderBTCf;
	private JLabel emaLongConstantl;
	private JTextField emaLongConstantf;
	private JLabel emaShortConstantl;
	private JTextField emaShortConstantf;
	private JLabel emaSignalConstantl;
	private JTextField emaSignalConstantf;
	private JLabel binanceCandlesticksPeriodl;
	private JTextField binanceCandlesticksPeriodf;
	private JLabel emaCountCandlesticksl;
	private JTextField emaCountCandlesticksf;
	private JLabel takeProfitPercentagel;
	private JTextField takeProfitPercentagef;
	private JLabel takeProfitInstaSellPercentagel;
	private JTextField takeProfitInstaSellPercentagef;
	private JLabel stopLossPercentagel;
	private JTextField stopLossPercentagef;
	private JLabel trailingStopTakeProfitPlusPercentageConstantl;
	private JTextField trailingStopStopLossMinusPercentageConstantf;
	private JLabel ignoreBlacklistl;
	private JTextField ignoreBlacklistf;
	private JLabel stopLossProtectionl;
	private JTextField stopLossProtectionf;
	private JLabel emaBuyRemoveLastOpenCandlestickl;
	private JTextField emaBuyRemoveLastOpenCandlestickf;
	private JLabel stopLossProtectionBuyRemoveLastOpenCandlestickl;
	private JTextField stopLossProtectionBuyRemoveLastOpenCandlestickf;
	private JLabel stopLossProtectionCloseNonActiveRemoveLastOpenCandlestickl;
	private JTextField stopLossProtectionCloseNonActiveRemoveLastOpenCandlestickf;
	private JLabel checkUptrendRemoveLastOpenCandlestickl;
	private JTextField checkUptrendRemoveLastOpenCandlestickf;
	private JLabel emaSellRemoveLastOpenCandlestickl;
	private JTextField emaSellRemoveLastOpenCandlestickf;
	private JLabel emaStrategySellLongIntolerantionPercentagel;
	private JTextField emaStrategySellLongIntolerantionPercentagef;
	private JLabel allowNewOrdersl;
	private JTextField allowNewOrdersf;
	private JLabel stopLossProtectionPercentageIntolerantionl;
	private JTextField stopLossProtectionPercentageIntolerantionf;
	private JLabel checkUptrendEmaStrategyl;
	private JTextField checkUptrendEmaStrategyf;
	private JLabel coinMachineOnl;
	private JTextField coinMachineOnf;
	private JLabel trailingStopStrategyl;
	private JTextField trailingStopStrategyf;
	private JLabel emaUptrendEmaStrategyCandlestickPeriodl;
	private JTextField emaUptrendEmaStrategyCandlestickPeriodf;
	private JLabel actualStrategyl;
	private JTextField actualStrategyf;
	private JLabel emaUptrendEmaStrategyShortEmal;
	private JTextField emaUptrendEmaStrategyShortEmaf;
	private JLabel emaUptrendEmaStrategyLongEmal;
	private JTextField emaUptrendEmaStrategyLongEmaf;
	private JLabel emaStrategyShortEmal;
	private JTextField emaStrategyShortEmaf;
	private JLabel emaStrategyLongEmal;
	private JTextField emaStrategyLongEmaf;
	private JLabel emaStrategyCandlestickCountl;
	private JTextField emaStrategyCandlestickCountf;
	private JLabel emaStrategyBuyLongIntolerantionPercentagel;
	private JTextField emaStrategyBuyLongIntolerantionPercentagef;
	private JLabel blackListCoinsl;
	private JTextField blackListCoinsf;

	private JLabel notificationEmailsl;
	private JTextField notificationEmailsf;
	private JLabel notificationOnErrorEnabledl;
	private JTextField notificationOnErrorEnabledf;
	private JLabel emaUptrendEmaStrategyCandlestickCountl;
	private JTextField emaUptrendEmaStrategyCandlestickCountf;
	private JLabel emaStrategyBuyWaitCrossl;
	private JTextField emaStrategyBuyWaitCrossf;
	private JLabel setTrailingStopAfterEmaCrossedDownl;
	private JTextField setTrailingStopAfterEmaCrossedDownf;


	JPanel mainframe = new JPanel();
	GridLayout experimentLayout = new GridLayout(0, 2);

	private CoreEngine coreEngine;

	public UIController(final String title) {
		super(title);
		mainframe.setLayout(experimentLayout);


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
				makeNewOrderButton.setText("Make new orders " + coreEngine.propertyPlaceholder.isAllowNewOrders());
			}
		});
	}
}
