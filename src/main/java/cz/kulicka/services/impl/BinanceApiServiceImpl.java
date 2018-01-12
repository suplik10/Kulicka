package cz.kulicka.services.impl;

import cz.kulicka.services.WebApiService;
import org.apache.log4j.Logger;

public class BinanceApiServiceImpl implements WebApiService {

    static Logger log = Logger.getLogger(BinanceApiServiceImpl.class);

    @Override
    public Object getExchangeCurrencies() {
        return null;
    }

    @Override
    public String pingExchange() {
        return null;
    }
}
