package cz.kulicka.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import cz.kulicka.ExchangeCommandCenter;
import cz.kulicka.exception.BinanceApiException;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Current exchange trading rules and symbol information.
 * https://github.com/binance-exchange/binance-official-api-docs/blob/master/rest-api.md
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeInfo {

    static Logger log = LogManager.getLogger(ExchangeInfo.class);

    private String timezone;

    private Long serverTime;

    private List<RateLimit> rateLimits;

    // private List<String> exchangeFilters;

    private List<SymbolInfo> symbols;

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Long getServerTime() {
        return serverTime;
    }

    public void setServerTime(Long serverTime) {
        this.serverTime = serverTime;
    }

    public List<RateLimit> getRateLimits() {
        return rateLimits;
    }

    public void setRateLimits(List<RateLimit> rateLimits) {
        this.rateLimits = rateLimits;
    }

    public List<SymbolInfo> getSymbols() {
        return symbols;
    }

    public void setSymbols(List<SymbolInfo> symbols) {
        this.symbols = symbols;
    }

    /**
     * @param symbol the symbol to obtain information for (e.g. ETHBTC)
     * @return symbol exchange information
     */
    public SymbolInfo getSymbolInfo(String symbol) {
        return symbols.stream().filter(symbolInfo -> symbolInfo.getSymbol().equals(symbol))
                .findFirst()
                .orElseThrow(() -> new BinanceApiException("Unable to obtain information for symbol " + symbol));
    }

    public int getNumberOfDecimalPlacesToOrder(String symbolPair) {

        SymbolInfo symbolInfo = symbols.stream().filter(symbolInfo1 -> symbolInfo1.getSymbol().equals(symbolPair))
                .findFirst()
                .orElseThrow(() -> new BinanceApiException("Unable to obtain information for symbol " + symbolPair));

        String stepSize = symbolInfo.getSymbolFilter(FilterType.LOT_SIZE).getStepSize();

        try{
            try{
                return stepSize.substring(stepSize.indexOf("."), stepSize.indexOf("1")).length();
            }catch (StringIndexOutOfBoundsException e){
                return 0;
            }
        }catch (RuntimeException e){
            log.error("Failed to parse coin decimal places > coin symbol " + symbolPair + " EXCEPTION: " + e.getStackTrace());
            throw new BinanceApiException("Failed to parse coin decimal places > coin symbol " + symbolPair);
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("timezone", timezone)
                .append("serverTime", serverTime)
                .append("rateLimits", rateLimits)
                .append("symbols", symbols)
                .toString();
    }
}
