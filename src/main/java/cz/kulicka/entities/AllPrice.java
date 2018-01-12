package cz.kulicka.entities;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "symbol",
        "bidPrice",
        "bidQty",
        "askPrice",
        "askQty"
})
public class AllPrice {

    @JsonProperty("symbol")
    private String symbol;
    @JsonProperty("bidPrice")
    private String bidPrice;
    @JsonProperty("bidQty")
    private String bidQty;
    @JsonProperty("askPrice")
    private String askPrice;
    @JsonProperty("askQty")
    private String askQty;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("symbol")
    public String getSymbol() {
        return symbol;
    }

    @JsonProperty("symbol")
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @JsonProperty("bidPrice")
    public String getBidPrice() {
        return bidPrice;
    }

    @JsonProperty("bidPrice")
    public void setBidPrice(String bidPrice) {
        this.bidPrice = bidPrice;
    }

    @JsonProperty("bidQty")
    public String getBidQty() {
        return bidQty;
    }

    @JsonProperty("bidQty")
    public void setBidQty(String bidQty) {
        this.bidQty = bidQty;
    }

    @JsonProperty("askPrice")
    public String getAskPrice() {
        return askPrice;
    }

    @JsonProperty("askPrice")
    public void setAskPrice(String askPrice) {
        this.askPrice = askPrice;
    }

    @JsonProperty("askQty")
    public String getAskQty() {
        return askQty;
    }

    @JsonProperty("askQty")
    public void setAskQty(String askQty) {
        this.askQty = askQty;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}