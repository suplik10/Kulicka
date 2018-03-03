package cz.kulicka.entity;

import java.util.ArrayList;

public class TradingData {

    ArrayList<Float> emaShort;
    ArrayList<Float> emaLong;
    ArrayList<Float> MACDLine;
    ArrayList<Float> emaSignal;
    ArrayList<Float> MACDHistogram;
    private float emaLongYesterday;
    private float emaShortYesterday;
    private float emaSignalYesterday;
    private float lastMacdHistogram;
    private float preLastMacdHistogram;

    public TradingData() {
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
        this.emaLongYesterday = emaLong.get(emaLong.size() - 1);
        this.emaShortYesterday = emaShort.get(emaShort.size() - 1);
        this.emaSignalYesterday = emaSignal.get(emaSignal.size() - 1);
        this.lastMacdHistogram = MACDHistogram.get(MACDHistogram.size() - 1);
        if (MACDHistogram.size() > 1) {
            this.preLastMacdHistogram = MACDHistogram.get(MACDHistogram.size() - 2);
        }
    }

    public float getEmaLongYesterday() {
        return emaLongYesterday;
    }

    public float getEmaShortYesterday() {
        return emaShortYesterday;
    }

    public float getLastMacdHistogram() {
        return lastMacdHistogram;
    }

    public float getPreLastMacdHistogram() {
        return preLastMacdHistogram;
    }

    public float getEmaSignalYesterday() {
        return emaSignalYesterday;
    }
}
