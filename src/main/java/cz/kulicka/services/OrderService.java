package cz.kulicka.services;

import cz.kulicka.entities.Order;

import java.util.List;

public interface OrderService {

    void create(Order order);

    List<Order> getAll();

    void saveAll(List<Order> orders);

    List<Order> getAllActive();

    void update(Order order);

    Order getOrderById(long id);

    void delete(long id);
}
