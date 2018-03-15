package cz.kulicka.entity;

import javax.persistence.*;
import java.util.ArrayList;

@Entity
@Table(name = "macd_tb")
public class MacdIndicator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long orderId;
    private String symbol;
    private long buyTime;
    private long sellTime;
    private float macdBuy;
    private float macdSell;
    private float emaShortYesterday;
    private float emaLongYesterday;
    private float emaSignalYesterday;

    @Lob
    @Column
    private byte[] macdBlobList;
    @Transient
    private ArrayList<Float> macdList;

    public MacdIndicator() {
    }

    public static MacdIndicator createNewInstance(MacdIndicator oldMacdIndicator, Long newOrderId, long newBuyTime, float newMacdBuy) {
        MacdIndicator newInstance = new MacdIndicator();
        newInstance.setSymbol(oldMacdIndicator.getSymbol());
        newInstance.setOrderId(newOrderId);
        newInstance.setBuyTime(newBuyTime);
        newInstance.setMacdBuy(newMacdBuy);
        newInstance.setEmaShortYesterday(oldMacdIndicator.getEmaShortYesterday());
        newInstance.setEmaLongYesterday(oldMacdIndicator.getEmaLongYesterday());
        newInstance.setEmaSignalYesterday(oldMacdIndicator.getEmaSignalYesterday());
        newInstance.setMacdBlobList(oldMacdIndicator.getMacdBlobList());
        newInstance.setMacdList(oldMacdIndicator.getMacdList());

        return newInstance;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public float getEmaShortYesterday() {
        return emaShortYesterday;
    }

    public void setEmaShortYesterday(float emaShortYesterday) {
        this.emaShortYesterday = emaShortYesterday;
    }

    public float getEmaLongYesterday() {
        return emaLongYesterday;
    }

    public void setEmaLongYesterday(float emaLongYesterday) {
        this.emaLongYesterday = emaLongYesterday;
    }

    public ArrayList<Float> getMacdList() {
        return macdList;
    }

    public void setMacdList(ArrayList<Float> macdList) {
        this.macdList = macdList;
    }

    public byte[] getMacdBlobList() {
        return macdBlobList;
    }

    public void setMacdBlobList(byte[] macdBlobList) {
        this.macdBlobList = macdBlobList;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
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

    public float getMacdBuy() {
        return macdBuy;
    }

    public void setMacdBuy(float macdBuy) {
        this.macdBuy = macdBuy;
    }

    public float getMacdSell() {
        return macdSell;
    }

    public void setMacdSell(float macdSell) {
        this.macdSell = macdSell;
    }

    public float getEmaSignalYesterday() {
        return emaSignalYesterday;
    }

    public void setEmaSignalYesterday(float emaSignalYesterday) {
        this.emaSignalYesterday = emaSignalYesterday;
    }

    @Override
    public String toString() {
        return "MacdIndicator{" +
                "orderId=" + orderId +
                ", symbol='" + symbol + '\'' +
                ", buyTime=" + buyTime +
                ", sellTime=" + sellTime +
                ", macdBuy=" + macdBuy +
                ", macdSell=" + macdSell +
                ", emaShortYesterday=" + emaShortYesterday +
                ", emaLongYesterday=" + emaLongYesterday +
                ", emaSignalYesterday=" + emaSignalYesterday +
                ", macdList=" + macdList.toString() +
                '}';
    }
}
