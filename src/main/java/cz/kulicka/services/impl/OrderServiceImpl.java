package cz.kulicka.services.impl;


import cz.kulicka.CoreEngine;
import cz.kulicka.entities.Order;
import cz.kulicka.repository.OrderRepository;
import cz.kulicka.services.OrderService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    static Logger log = Logger.getLogger(OrderServiceImpl.class);

    @Autowired
    OrderRepository orderRepository;

    @Override
    public void create(Order order) {
        orderRepository.save(order);
        log.info("Currency " + order.getSymbol() + " [[[[MAKE ORDER for " + order.getBuyPrice() + "BTC ]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]");




    }

    @Override
    public List<Order> getAll() {
        return (List<Order>) orderRepository.findAll();
    }

    @Override
    public void saveAll(List<Order> orders) {
        orderRepository.save(orders);
    }


    @Override
    public List<Order> getAllActive() {
        return (List<Order>) orderRepository.findAllByActiveTrue();
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
