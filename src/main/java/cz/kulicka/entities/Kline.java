package cz.kulicka.entities;

import org.junit.Assert;

import java.util.ArrayList;

public class Kline {

    private long openTime;
    private double open;
    private double high;
    private double low;
    private double close;
    private double volume;
    private long closeTime;
    private double quoteAssetVolume;
    private int numberOfTrades;
    private double takerBuyBaseAssetVolume;
    private double takerBuyQuoteAssetVolume;
    private double canBeIgnored;

    public Kline(ArrayList<String> klineDataFromJson) {
        Assert.assertNotNull(klineDataFromJson);
        Assert.assertEquals(12, klineDataFromJson.size());

        this.openTime = Long.parseLong(klineDataFromJson.get(0));
        this.open = Double.parseDouble(klineDataFromJson.get(1));
        this.high = Double.parseDouble(klineDataFromJson.get(2));
        this.low = Double.parseDouble(klineDataFromJson.get(3));
        this.close = Double.parseDouble(klineDataFromJson.get(4));
        this.volume = Double.parseDouble(klineDataFromJson.get(5));
        this.closeTime = Long.parseLong(klineDataFromJson.get(6));
        this.quoteAssetVolume = Double.parseDouble(klineDataFromJson.get(7));
        this.openTime = Integer.parseInt(klineDataFromJson.get(8));
        this.takerBuyBaseAssetVolume = Double.parseDouble(klineDataFromJson.get(9));
        this.takerBuyQuoteAssetVolume = Double.parseDouble(klineDataFromJson.get(10));
        this.canBeIgnored = Double.parseDouble(klineDataFromJson.get(11));

    }

    public long getOpenTime() {
        return openTime;
    }

    public void setOpenTime(long openTime) {
        this.openTime = openTime;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public long getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(long closeTime) {
        this.closeTime = closeTime;
    }

    public double getQuoteAssetVolume() {
        return quoteAssetVolume;
    }

    public void setQuoteAssetVolume(double quoteAssetVolume) {
        this.quoteAssetVolume = quoteAssetVolume;
    }

    public int getNumberOfTrades() {
        return numberOfTrades;
    }

    public void setNumberOfTrades(int numberOfTrades) {
        this.numberOfTrades = numberOfTrades;
    }

    public double getTakerBuyBaseAssetVolume() {
        return takerBuyBaseAssetVolume;
    }

    public void setTakerBuyBaseAssetVolume(double takerBuyBaseAssetVolume) {
        this.takerBuyBaseAssetVolume = takerBuyBaseAssetVolume;
    }

    public double getTakerBuyQuoteAssetVolume() {
        return takerBuyQuoteAssetVolume;
    }

    public void setTakerBuyQuoteAssetVolume(double takerBuyQuoteAssetVolume) {
        this.takerBuyQuoteAssetVolume = takerBuyQuoteAssetVolume;
    }

    public double getCanBeIgnored() {
        return canBeIgnored;
    }

    public void setCanBeIgnored(double canBeIgnored) {
        this.canBeIgnored = canBeIgnored;
    }
}
