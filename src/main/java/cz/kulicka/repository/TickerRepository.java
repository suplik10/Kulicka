package cz.kulicka.repository;

import cz.kulicka.entities.Ticker;
import org.springframework.data.repository.CrudRepository;

public interface TickerRepository extends CrudRepository<Ticker, Long> {

}
