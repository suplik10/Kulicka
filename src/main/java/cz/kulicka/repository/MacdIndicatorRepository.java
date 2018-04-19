package cz.kulicka.repository;

import cz.kulicka.entity.MacdIndicator;
import org.springframework.data.repository.CrudRepository;

public interface MacdIndicatorRepository extends CrudRepository<MacdIndicator, Long> {

    Iterable<MacdIndicator> findAllByOrderIdEquals(Long id);
}
