package cz.kulicka.mock.service;

import cz.kulicka.entity.Order;
import cz.kulicka.service.OrderService;

import java.util.ArrayList;
import java.util.List;

public class OrderServiceMock implements OrderService {
    @Override
    public Order create(Order order) {
        return order;
    }

    @Override
    public List<Order> getAll() {
        return null;
    }

    @Override
    public void saveAll(List<Order> orders) {

    }

    @Override
    public List<Order> getAllActive() {
        Order order = new Order();
        order.setSymbol("blablabla");
        ArrayList arrayList = new ArrayList();
        arrayList.add(order);

        return arrayList;
    }

    @Override
    public List<Order> getAllActiveBySymbol(String symbol) {
        return null;
    }

    @Override
    public List<Order> getAllInActive() {
        return null;
    }

    @Override
    public void update(Order order) {

    }

    @Override
    public Order getOrderById(long id) {
        return null;
    }

    @Override
    public void delete(long id) {

    }
}
