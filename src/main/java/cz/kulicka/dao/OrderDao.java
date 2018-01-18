package cz.kulicka.dao;

import cz.kulicka.entities.Order;

public interface OrderDao {

    void create(Order order);

    void update(Order order);

    Order getOrderById(long id);

    void delete(long id);
}
