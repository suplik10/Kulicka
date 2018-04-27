package cz.kulicka.test;

import cz.kulicka.entity.TradingData;
import cz.kulicka.util.MathUtil;

public class EmaTest {


	public void emaTest()

	{
		TradingData tradingData = null;//= getEmaTradingDataHistorical("ETHBTC", propertyPlaceholder.getBinanceCandlesticksPeriod(), propertyPlaceholder.getEmaStrategyCandlestickCount(),
		//propertyPlaceholder.getEmaStrategyShortEma(), propertyPlaceholder.getEmaStrategyLongEma(), propertyPlaceholder.isEmaBuyRemoveLastOpenCandlestick());


		for (int i = 0; i < tradingData.getEmaLong().size(); i++) {
			double longg = tradingData.getEmaLong().get(i).doubleValue();
			double shortt = tradingData.getEmaShort().get(i).doubleValue();

			boolean result;

			double propertyIntolerantion = -0.01;

			//test sell
			result = (MathUtil.getPercentageDifference(longg, shortt) + propertyIntolerantion) < 0;

			System.out.print(String.format("%.9f", shortt) + " " + String.format("%.9f", longg));
			if (result) {
				System.out.println(" true");
			} else {
				System.out.println(" ----- false");
			}
		}


	}
}
