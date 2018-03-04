package cz.kulicka.service.impl;

import com.google.common.collect.Iterables;
import cz.kulicka.entity.MacdIndicator;
import cz.kulicka.repository.MacdIndicatorRepository;
import cz.kulicka.service.MacdIndicatorService;
import cz.kulicka.util.MapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MacdIndicatorServiceImpl implements MacdIndicatorService {

    @Autowired
    MacdIndicatorRepository macdIndicatorRepository;


    @Override
    public void create(MacdIndicator macdIndicator) {
        if(macdIndicator.getMacdList() != null){
            String json = MapperUtil.listOfFloatToJson(macdIndicator.getMacdList());
            macdIndicator.setMacdBlobList(json.getBytes());
        }
        macdIndicatorRepository.save(macdIndicator);
    }

    @Override
    public List<MacdIndicator> getAll() {
        List<MacdIndicator> macdIndicators = (List<MacdIndicator>) macdIndicatorRepository.findAll();
        mappArray(macdIndicators);
        return macdIndicators;
    }

    @Override
    public void saveAll(List<MacdIndicator> macdIndicators) {

    }

    @Override
    public void update(MacdIndicator macdIndicator) {
        if(macdIndicator.getMacdList() != null){
            String json = MapperUtil.listOfFloatToJson(macdIndicator.getMacdList());
            macdIndicator.setMacdBlobList(json.getBytes());
        }
        macdIndicatorRepository.save(macdIndicator);
    }

    @Override
    public MacdIndicator getMacdIndicatorByOrderId(long id) {
        List<MacdIndicator> macdIndicators = (List<MacdIndicator>) macdIndicatorRepository.findAllByOrderIdEquals(id);
        mappArray(macdIndicators);
        return Iterables.getFirst(macdIndicators,null);
    }

    @Override
    public void delete(long id) {

    }

    private void mappArray (List<MacdIndicator> macdIndicators){
        for (MacdIndicator macdIndicator : macdIndicators){
            if(macdIndicator.getMacdBlobList() != null) {
                String json = new String(macdIndicator.getMacdBlobList());
                macdIndicator.setMacdList(MapperUtil.jsonToListOfFloat(json));
            }
        }
    }
}
