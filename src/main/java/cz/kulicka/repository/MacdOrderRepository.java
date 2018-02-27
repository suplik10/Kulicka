package cz.kulicka.repository;

import cz.kulicka.entity.MacdOrder;
import cz.kulicka.entity.Order;
import org.springframework.data.repository.CrudRepository;

public interface MacdOrderRepository extends CrudRepository<MacdOrder, Long> {

    Iterable<MacdOrder> findAllBySymbolEquals(String symbol);
}
