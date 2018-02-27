package cz.kulicka.services;

import cz.kulicka.entity.MacdIndicator;

import java.util.List;

public interface MacdIndicatorService {

    void create(MacdIndicator macdIndicator);

    List<MacdIndicator> getAll();

    void saveAll(List<MacdIndicator> macdIndicators);

    void update(MacdIndicator macdIndicator);

    MacdIndicator getMacdIndicatorByOrderId(long id);

    void delete(long id);
}
