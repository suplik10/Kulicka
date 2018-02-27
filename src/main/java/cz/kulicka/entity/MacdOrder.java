package cz.kulicka.entity;

import javax.persistence.*;
import java.sql.Clob;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "macd_tb")
public class MacdOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String symbol;
    private Date buyTime;
    private Date sellTime;
    private float macdBuy;
    private float macdSell;
    private String macdJsonList;


    public MacdOrder() {
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Date getBuyTime() {
        return buyTime;
    }

    public void setBuyTime(Date buyTime) {
        this.buyTime = buyTime;
    }

    public Date getSellTime() {
        return sellTime;
    }

    public void setSellTime(Date sellTime) {
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

    public String getMacdJsonList() {
        return macdJsonList;
    }

    public void setMacdJsonList(String macdJsonList) {
        this.macdJsonList = macdJsonList;
    }
}
