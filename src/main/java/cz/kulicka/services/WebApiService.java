package cz.kulicka.services;

public interface WebApiService<T> {

    T getExchangeCurrencies();

    String pingExchange();

}
