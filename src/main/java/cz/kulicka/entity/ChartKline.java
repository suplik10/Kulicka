package cz.kulicka.entity;

import java.util.Date;

public class ChartKline {

    private float value;
    private double closedPrice;
    Date closedDate;


    public ChartKline() {
    }


    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public double getClosedPrice() {
        return closedPrice;
    }

    public void setClosedPrice(double closedPrice) {
        this.closedPrice = closedPrice;
    }

    public Date getClosedDate() {
        return closedDate;
    }

    public void setClosedDate(Date closedDate) {
        this.closedDate = closedDate;
    }
}
