package cz.kulicka.services;

import cz.kulicka.entity.Order;

import java.util.List;

public interface OrderService {

    Order create(Order order);

    List<Order> getAll();

    void saveAll(List<Order> orders);

    List<Order> getAllActive();

    List<Order> getAllInActive();

    void update(Order order);

    Order getOrderById(long id);

    void delete(long id);
}
