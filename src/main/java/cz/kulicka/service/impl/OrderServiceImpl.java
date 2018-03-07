package cz.kulicka.service.impl;


import cz.kulicka.entity.Order;
import cz.kulicka.repository.OrderRepository;
import cz.kulicka.service.OrderService;
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
    public Order create(Order order) {
        log.info("[MAKE ORDER for " + order.toString() + " ]");
        return orderRepository.save(order);
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
    public List<Order> getAllActiveBySymbol(String symbol) {
        return (List<Order>) orderRepository.findAllByActiveTrueAndSymbolEquals(symbol);
    }

    @Override
    public List<Order> getAllInActive() {
        return (List<Order>) orderRepository.findAllByActiveFalseAndSellPriceForOrderWithFeeIsNotNull();
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
