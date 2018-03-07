package cz.kulicka.repository;

import cz.kulicka.entity.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, Long> {

    Iterable<Order> findAllByActiveTrue();

    Iterable<Order> findAllByActiveTrueAndSymbolEquals(String symbol);

    Iterable<Order> findAllByActiveFalseAndSellPriceForOrderWithFeeIsNotNull();
}
