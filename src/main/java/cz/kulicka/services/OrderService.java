package cz.kulicka.services;

import cz.kulicka.entities.Order;

public interface OrderService {

    void create(Order order);

    void update(Order order);

    Order getOrderById(long id);

    void delete(long id);
}
