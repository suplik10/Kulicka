package cz.kulicka.entity;

public class EmaMacd {

    private float emaLongYesterday;
    private float emaShortYesterday;
    private float lastMacdHistogram;
    private float preLastMacdHistogram;

    public EmaMacd(float emaLongYesterday, float emaShortYesterday, float lastMacdHistogram, float preLastMacdHistogram) {
        this.emaLongYesterday = emaLongYesterday;
        this.emaShortYesterday = emaShortYesterday;
        this.lastMacdHistogram = lastMacdHistogram;
        this.preLastMacdHistogram = preLastMacdHistogram;
    }

    public float getEmaLongYesterday() {
        return emaLongYesterday;
    }

    public void setEmaLongYesterday(float emaLongYesterday) {
        this.emaLongYesterday = emaLongYesterday;
    }

    public float getEmaShortYesterday() {
        return emaShortYesterday;
    }

    public void setEmaShortYesterday(float emaShortYesterday) {
        this.emaShortYesterday = emaShortYesterday;
    }

    public float getLastMacdHistogram() {
        return lastMacdHistogram;
    }

    public void setLastMacdHistogram(float lastMacdHistogram) {
        this.lastMacdHistogram = lastMacdHistogram;
    }

    public float getPreLastMacdHistogram() {
        return preLastMacdHistogram;
    }

    public void setPreLastMacdHistogram(float preLastMacdHistogram) {
        this.preLastMacdHistogram = preLastMacdHistogram;
    }
}
