package cz.kulicka.entities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import cz.kulicka.entities.Currency;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"Markets"})
public class Market {

    @JsonProperty("Markets")
    private List<Currency> markets = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("Markets")
    public List<Currency> getMarkets() {
        return markets;
    }

    @JsonProperty("Markets")
    public void setMarkets(List<Currency> markets) {
        this.markets = markets;
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