package cz.kulicka.entity;

import com.google.common.collect.Iterables;

import java.util.ArrayList;

public class TradingData {

    private String symbol;
    private Long orderId;
    private ArrayList<Float> emaShort;
    private ArrayList<Float> emaLong;
    private ArrayList<Float> MACDLine;
    private ArrayList<Float> emaSignal;
    private ArrayList<Float> MACDHistogram;
    private float lastEmaLongYesterday;
    private float prelastEmaLongYesterday;
    private float lastEmaShortYesterday;
    private float preLastEmaShortYesterday;
    private float lastEmaSignalYesterday;
    private float preLastEmaSignalYesterday;
    private float lastMacdHistogram;
    private float preLastMacdHistogram;

    public TradingData(String symbol, Long orderId) {
        this.symbol = symbol;
        this.orderId = orderId;
    }

    public ArrayList<Float> getEmaShort() {
        return emaShort;
    }

    public void setEmaShort(ArrayList<Float> emaShort) {
        this.emaShort = emaShort;
    }

    public ArrayList<Float> getEmaLong() {
        return emaLong;
    }

    public void setEmaLong(ArrayList<Float> emaLong) {
        this.emaLong = emaLong;
    }

    public ArrayList<Float> getMACDLine() {
        return MACDLine;
    }

    public void setMACDLine(ArrayList<Float> MACDLine) {
        this.MACDLine = MACDLine;
    }

    public ArrayList<Float> getEmaSignal() {
        return emaSignal;
    }

    public void setEmaSignal(ArrayList<Float> emaSignal) {
        this.emaSignal = emaSignal;
    }

    public ArrayList<Float> getMACDHistogram() {
        return MACDHistogram;
    }

    public void setMACDHistogram(ArrayList<Float> MACDHistogram) {
        this.MACDHistogram = MACDHistogram;
    }

    public void updateFields() {
        if(emaLong != null){
            this.lastEmaLongYesterday = emaLong.get(emaLong.size() - 1);

            if (emaLong.size() > 1) {
                this.prelastEmaLongYesterday = emaLong.get(emaLong.size() - 2);
            }
        }

        if(emaShort != null) {
            this.lastEmaShortYesterday = emaShort.get(emaShort.size() - 1);

            if (emaShort.size() > 1) {
                this.preLastEmaShortYesterday = emaShort.get(emaShort.size() - 2);
            }
        }

        if(emaSignal != null) {
            this.lastEmaSignalYesterday = emaSignal.get(emaSignal.size() - 1);

            if (emaSignal.size() > 1) {
                this.preLastEmaSignalYesterday = emaSignal.get(emaSignal.size() - 2);
            }
        }

        if(MACDHistogram != null) {
            this.lastMacdHistogram = MACDHistogram.get(MACDHistogram.size() - 1);

            if (MACDHistogram.size() > 1) {
                this.preLastMacdHistogram = MACDHistogram.get(MACDHistogram.size() - 2);
            }
        }
    }

    public float getPrelastEmaLongYesterday() {
        return prelastEmaLongYesterday;
    }

    public float getPreLastEmaShortYesterday() {
        return preLastEmaShortYesterday;
    }

    public float getLastEmaSignalYesterday() {
        return lastEmaSignalYesterday;
    }

    public float getPreLastEmaSignalYesterday() {
        return preLastEmaSignalYesterday;
    }

    public float getLastEmaLongYesterday() {
        return lastEmaLongYesterday;
    }

    public float getLastEmaShortYesterday() {
        return lastEmaShortYesterday;
    }

    public float getLastMacdHistogram() {
        return lastMacdHistogram;
    }

    public float getPreLastMacdHistogram() {
        return preLastMacdHistogram;
    }

    @Override
    public String toString() {
        return "TradingData{" +
                "symbol='" + symbol + '\'' +
                ", orderId=" + orderId +
                ", lastEmaLongYesterday=" + lastEmaLongYesterday +
                ", prelastEmaLongYesterday=" + prelastEmaLongYesterday +
                ", lastEmaShortYesterday=" + lastEmaShortYesterday +
                ", preLastEmaShortYesterday=" + preLastEmaShortYesterday +
                ", lastEmaSignalYesterday=" + lastEmaSignalYesterday +
                ", preLastEmaSignalYesterday=" + preLastEmaSignalYesterday +
                ", lastMacdHistogram=" + lastMacdHistogram +
                ", preLastMacdHistogram=" + preLastMacdHistogram +
                '}';
    }

    public String getSymbol() {
        return symbol;
    }

    public Long getOrderId() {
        return orderId;
    }
}
