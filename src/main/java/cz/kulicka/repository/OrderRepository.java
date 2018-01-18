package cz.kulicka.repository;

import cz.kulicka.entities.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, Long> {

    Iterable<Order> findAllByActiveTrue();
}
