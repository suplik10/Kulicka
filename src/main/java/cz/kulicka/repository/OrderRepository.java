package cz.kulicka.repository;

import cz.kulicka.entity.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, Long> {

    Iterable<Order> findAllByActiveTrue();

    Order findByIdEquals(long id);

    Iterable<Order> findAllByActiveTrueAndSymbolEquals(String symbol);

    Iterable<Order> findAllByOpenTrueAndSymbolEquals(String symbol);

    Iterable<Order> findAllByOpenTrueAndActiveFalse();

    Iterable<Order> findAllByActiveTrueAndOpenFalseAndSymbolEquals(String symbol);

    Iterable<Order> findAllByOpenTrueAndActiveFalseAndSymbolEquals(String symbol);

    Iterable<Order> findAllByActiveFalseAndSellPriceForOrderWithFeeIsNotNull();

    Iterable<Order> findAllByActiveFalse();

    Iterable<Order> findAllByOpenTrue();


}
