package cz.kulicka.entity;

public class EmaMacd {

    private float emaLongYesterday;
    private float emaShortYesterday;
    private float lastMacd;
    private float preLastMacd;

    public EmaMacd(float emaLongYesterday, float emaShortYesterday, float lastMacd, float preLastMacd) {
        this.emaLongYesterday = emaLongYesterday;
        this.emaShortYesterday = emaShortYesterday;
        this.lastMacd = lastMacd;
        this.preLastMacd = preLastMacd;
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

    public float getLastMacd() {
        return lastMacd;
    }

    public void setLastMacd(float lastMacd) {
        this.lastMacd = lastMacd;
    }

    public float getPreLastMacd() {
        return preLastMacd;
    }

    public void setPreLastMacd(float preLastMacd) {
        this.preLastMacd = preLastMacd;
    }
}
