package cz.kulicka.entities;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "Label",
        "Name",
        "Price_btc",
        "Price_usd",
        "Price_cny",
        "Price_eur",
        "Price_gbp",
        "Price_rur",
        "Volume_24h",
        "Timestamp"
})
public class Currency {

    @JsonProperty("Label")
    private String label;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("Price_btc")
    private Double priceBtc;
    @JsonProperty("Price_usd")
    private Double priceUsd;
    @JsonProperty("Price_cny")
    private Double priceCny;
    @JsonProperty("Price_eur")
    private Double priceEur;
    @JsonProperty("Price_gbp")
    private Double priceGbp;
    @JsonProperty("Price_rur")
    private Double priceRur;
    @JsonProperty("Volume_24h")
    private Double volume24h;
    @JsonProperty("Timestamp")
    private Integer timestamp;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("Label")
    public String getLabel() {
        return label;
    }

    @JsonProperty("Label")
    public void setLabel(String label) {
        this.label = label;
    }

    @JsonProperty("Name")
    public String getName() {
        return name;
    }

    @JsonProperty("Name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("Price_btc")
    public Double getPriceBtc() {
        return priceBtc;
    }

    @JsonProperty("Price_btc")
    public void setPriceBtc(Double priceBtc) {
        this.priceBtc = priceBtc;
    }

    @JsonProperty("Price_usd")
    public Double getPriceUsd() {
        return priceUsd;
    }

    @JsonProperty("Price_usd")
    public void setPriceUsd(Double priceUsd) {
        this.priceUsd = priceUsd;
    }

    @JsonProperty("Price_cny")
    public Double getPriceCny() {
        return priceCny;
    }

    @JsonProperty("Price_cny")
    public void setPriceCny(Double priceCny) {
        this.priceCny = priceCny;
    }

    @JsonProperty("Price_eur")
    public Double getPriceEur() {
        return priceEur;
    }

    @JsonProperty("Price_eur")
    public void setPriceEur(Double priceEur) {
        this.priceEur = priceEur;
    }

    @JsonProperty("Price_gbp")
    public Double getPriceGbp() {
        return priceGbp;
    }

    @JsonProperty("Price_gbp")
    public void setPriceGbp(Double priceGbp) {
        this.priceGbp = priceGbp;
    }

    @JsonProperty("Price_rur")
    public Double getPriceRur() {
        return priceRur;
    }

    @JsonProperty("Price_rur")
    public void setPriceRur(Double priceRur) {
        this.priceRur = priceRur;
    }

    @JsonProperty("Volume_24h")
    public Double getVolume24h() {
        return volume24h;
    }

    @JsonProperty("Volume_24h")
    public void setVolume24h(Double volume24h) {
        this.volume24h = volume24h;
    }

    @JsonProperty("Timestamp")
    public Integer getTimestamp() {
        return timestamp;
    }

    @JsonProperty("Timestamp")
    public void setTimestamp(Integer timestamp) {
        this.timestamp = timestamp;
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

