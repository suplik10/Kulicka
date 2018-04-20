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
    public List<Order> getAllOpenBySymbol(String symbol) {
        return (List<Order>) orderRepository.findAllByOpenTrueAndSymbolEquals(symbol);
    }

    @Override
    public List<Order> getAllOpenButNotActive() {
        return (List<Order>) orderRepository.findAllByOpenTrueAndActiveFalse();
    }

    @Override
    public List<Order> getAllActiveButNotOpenBySymbol(String symbol) {
        return (List<Order>) orderRepository.findAllByActiveTrueAndOpenFalseAndSymbolEquals(symbol);
    }

    @Override
    public List<Order> getAllOpenButNotActiveBySymbol(String symbol) {
        return (List<Order>) orderRepository.findAllByOpenTrueAndActiveFalseAndSymbolEquals(symbol);
    }

    @Override
    public List<Order> getAllInActive() {
        return (List<Order>) orderRepository.findAllByActiveFalseAndSellPriceForOrderWithFeeIsNotNull();
    }

    @Override
    public void update(Order order) {
        orderRepository.save(order);
    }

    @Override
    public Order getOrderById(long id) {
        return orderRepository.findByIdEquals(id);
    }

    @Override
    public void delete(long id) {

    }

    @Override
    public List<Order> findAllByActiveFalse() {
        return (List<Order>) orderRepository.findAllByActiveFalse();
    }

    @Override
    public List<Order> findAllByOpenTrue() {
        return (List<Order>) orderRepository.findAllByOpenTrue();
    }

    @Override
    public List<Order> findAllByActiveFalseAndSellPriceForOrderWithFeeIsNotNull() {
        return (List<Order>) orderRepository.findAllByActiveFalseAndSellPriceForOrderWithFeeIsNotNull();
    }
}
