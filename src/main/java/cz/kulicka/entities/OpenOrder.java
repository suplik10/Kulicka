package cz.kulicka.entities;

import java.io.Serializable;

public class OpenOrder implements Serializable {

    private String symbol;
    private double buyPrice;
    private double sellPrice;
    private boolean isActive;
    private int riskValue;

    public OpenOrder(String symbol, double buyPrice) {
        this.symbol = symbol;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.isActive = isActive;
        this.riskValue = riskValue;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(double buyPrice) {
        this.buyPrice = buyPrice;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getRiskValue() {
        return riskValue;
    }

    public void setRiskValue(int riskValue) {
        this.riskValue = riskValue;
    }

    @Override
    public String toString() {
        return "OpenOrder{" +
                "symbol='" + symbol + '\'' +
                ", buyPrice=" + buyPrice +
                ", sellPrice=" + sellPrice +
                ", isActive=" + isActive +
                ", riskValue=" + riskValue +
                '}';
    }
}
