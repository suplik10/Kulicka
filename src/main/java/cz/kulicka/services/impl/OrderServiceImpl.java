package cz.kulicka.services.impl;


import cz.kulicka.dao.OrderDao;
import cz.kulicka.entities.Order;
import cz.kulicka.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderDao orderDao;

    @Override
    public void create(Order order) {
        orderDao.create(order);
    }

    @Override
    public void update(Order order) {
        orderDao.update(order);
    }

    @Override
    public Order getOrderById(long id) {
        return orderDao.getOrderById(id);
    }

    @Override
    public void delete(long id) {
        orderDao.delete(id);
    }
}
