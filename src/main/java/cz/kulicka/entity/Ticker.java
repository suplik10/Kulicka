package cz.kulicka.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ticker_tb")
public class Ticker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String symbol;


    public Ticker() {
    }

    public Ticker(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public boolean equals(Object obj){
        if (obj == null)
            return false;

        Ticker objTicker = (Ticker) obj;

        if(this.getSymbol().equals(objTicker.getSymbol())){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public String toString() {
        return "Ticker{" +
                "symbol='" + symbol + '\'' +
                '}';
    }
}
