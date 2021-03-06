package cz.kulicka.service;

import cz.kulicka.entity.Order;

import java.util.List;

public interface OrderService {

    Order create(Order order);

    List<Order> getAll();

    void saveAll(List<Order> orders);

    List<Order> getAllActive();

    List<Order> getAllActiveBySymbol(String symbol);

    List<Order> getAllOpenBySymbol(String symbol);

    List<Order> getAllOpenButNotActive();

    List<Order> getAllActiveButNotOpenBySymbol(String symbol);

    List<Order> getAllOpenButNotActiveBySymbol(String symbol);

    List<Order> getAllInActive();

    void update(Order order);

    Order getOrderById(long id);

    void delete(long id);

    List<Order> findAllByActiveFalse();

    List<Order> findAllByOpenTrue();

    List<Order> findAllByActiveFalseAndSellPriceForOrderWithFeeIsNotNull();

}
