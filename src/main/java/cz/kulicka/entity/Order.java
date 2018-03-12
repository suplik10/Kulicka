package cz.kulicka.entity;


import javax.persistence.*;

@Entity
@Table(name = "order_tb")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String symbol;
    private double buyPriceForUnit;
    private double buyPriceBTCForUnit;
    private double sellPriceBTCForUnit;
    private double percentageProfitBTCForUnitWithoutFee;
    private double steppedBuyPriceForOrderWithFee;
    private double buyPriceForOrder;
    private double buyPriceForOrderWithFee;
    private double orderSumPriceInBoughtCurrencyWithFee;
    private double boughtAmount;
    private double sellAmount;
    private double sellPriceForOrderWithFee;
    private boolean active;
    private int riskValue;
    private double profitFeeIncluded;
    private double percentageProfitFeeIncluded;
    private long buyTime;
    private long sellTime;
    private double buyFee;
    private double buyFeeConstant;
    private double sellFee;
    private double sellFeeConstant;
    private int sellReason;


    public Order() {
    }

    public Order(String symbol, long buyTime, double buyPriceForOrder, double buyPriceForUnit, double buyFeeConstant, double sellFeeConstant, double buyPriceBTCForUnit) {
        this.symbol = symbol;
        this.buyPriceForOrder = buyPriceForOrder;
        this.buyFee = (buyPriceForOrder * (buyFeeConstant / 100));
        this.buyPriceForOrderWithFee = buyPriceForOrder - buyFee;
        this.boughtAmount = buyPriceForOrderWithFee / buyPriceForUnit;
        this.orderSumPriceInBoughtCurrencyWithFee = boughtAmount * buyPriceForUnit;
        this.buyFeeConstant = buyFeeConstant;
        this.sellFeeConstant = sellFeeConstant;
        this.buyPriceForUnit = buyPriceForUnit;
        this.steppedBuyPriceForOrderWithFee = buyPriceForOrderWithFee;
        this.buyTime = buyTime;
        this.buyPriceBTCForUnit = buyPriceBTCForUnit;
    }

    public double getBoughtAmount() {
        return boughtAmount;
    }

    public void setBoughtAmount(double boughtAmount) {
        this.boughtAmount = boughtAmount;
    }

    public double getProfitFeeIncluded() {
        return profitFeeIncluded;
    }

    public long getBuyTime() {
        return buyTime;
    }

    public void setBuyTime(long buyTime) {
        this.buyTime = buyTime;
    }

    public long getSellTime() {
        return sellTime;
    }

    public void setSellTime(long sellTime) {
        this.sellTime = sellTime;
    }

    public void setProfitFeeIncluded(double profitFeeIncluded) {
        this.profitFeeIncluded = profitFeeIncluded;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getBuyPriceForUnit() {
        return buyPriceForUnit;
    }

    public void setBuyPriceForUnit(double buyPriceForUnit) {
        this.buyPriceForUnit = buyPriceForUnit;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getRiskValue() {
        return riskValue;
    }

    public void setRiskValue(int riskValue) {
        this.riskValue = riskValue;
    }

    public Long getId() {
        return id;
    }

    public double getSteppedBuyPriceForOrderWithFee() {
        return steppedBuyPriceForOrderWithFee;
    }

    public void setSteppedBuyPriceForOrderWithFee(double steppedBuyPriceForOrderWithFee) {
        this.steppedBuyPriceForOrderWithFee = steppedBuyPriceForOrderWithFee;
    }

    public double getBuyPriceForOrder() {
        return buyPriceForOrder;
    }

    public void setBuyPriceForOrder(double buyPriceForOrder) {
        this.buyPriceForOrder = buyPriceForOrder;
    }

    public double getBuyPriceForOrderWithFee() {
        return buyPriceForOrderWithFee;
    }

    public void setBuyPriceForOrderWithFee(double buyPriceForOrderWithFee) {
        this.buyPriceForOrderWithFee = buyPriceForOrderWithFee;
    }

    public double getOrderSumPriceInBoughtCurrencyWithFee() {
        return orderSumPriceInBoughtCurrencyWithFee;
    }

    public void setOrderSumPriceInBoughtCurrencyWithFee(double orderSumPriceInBoughtCurrencyWithFee) {
        this.orderSumPriceInBoughtCurrencyWithFee = orderSumPriceInBoughtCurrencyWithFee;
    }

    public double getSellAmount() {
        return sellAmount;
    }

    public void setSellAmount(double sellAmount) {
        this.sellAmount = sellAmount;
    }

    public double getBuyFee() {
        return buyFee;
    }

    public void setBuyFee(double buyFee) {
        this.buyFee = buyFee;
    }

    public double getBuyFeeConstant() {
        return buyFeeConstant;
    }

    public void setBuyFeeConstant(double buyFeeConstant) {
        this.buyFeeConstant = buyFeeConstant;
    }

    public double getSellFee() {
        return sellFee;
    }

    public void setSellFee(double sellFee) {
        this.sellFee = sellFee;
    }

    public double getSellFeeConstant() {
        return sellFeeConstant;
    }

    public void setSellFeeConstant(double sellFeeConstant) {
        this.sellFeeConstant = sellFeeConstant;
    }

    public double getSellPriceForOrderWithFee() {
        return sellPriceForOrderWithFee;
    }

    public void setSellPriceForOrderWithFee(double sellPriceForOrderWithFee) {
        this.sellPriceForOrderWithFee = sellPriceForOrderWithFee;
    }

    public int getSellReason() {
        return sellReason;
    }

    public void setSellReason(int sellReason) {
        this.sellReason = sellReason;
    }

    public double getPercentageProfitFeeIncluded() {
        return percentageProfitFeeIncluded;
    }

    public void setPercentageProfitFeeIncluded(double percentageProfitFeeIncluded) {
        this.percentageProfitFeeIncluded = percentageProfitFeeIncluded;
    }

    public double getBuyPriceBTCForUnit() {
        return buyPriceBTCForUnit;
    }

    public void setBuyPriceBTCForUnit(double buyPriceBTCForUnit) {
        this.buyPriceBTCForUnit = buyPriceBTCForUnit;
    }

    public double getSellPriceBTCForUnit() {
        return sellPriceBTCForUnit;
    }

    public void setSellPriceBTCForUnit(double sellPriceBTCForUnit) {
        this.sellPriceBTCForUnit = sellPriceBTCForUnit;
    }

    public double getPercentageProfitBTCForUnitWithoutFee() {
        return percentageProfitBTCForUnitWithoutFee;
    }

    public void setPercentageProfitBTCForUnitWithoutFee(double percentageProfitBTCForUnitWithoutFee) {
        this.percentageProfitBTCForUnitWithoutFee = percentageProfitBTCForUnitWithoutFee;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", symbol='" + symbol + '\'' +
                ", buyPriceBTCForUnit=" + String.format("%.9f",buyPriceBTCForUnit) +
                ", sellPriceBTCForUnit=" + String.format("%.9f",sellPriceBTCForUnit) +
                ", ProfitBTCForUnitWithoutFee=" + String.format("%.3f", percentageProfitBTCForUnitWithoutFee) +
                " %, ProfitFeeIncluded=" + String.format("%.3f",percentageProfitFeeIncluded) +
                " % }";
    }
}
