package at.bbrz.demo.service;

import at.bbrz.demo.model.Hund;
import at.bbrz.demo.repository.HundeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HundePermService implements HundeServiceInterface {
    private HundeRepository hundeRepository;

    @Autowired
    public HundePermService(HundeRepository hundeRepository) {
        this.hundeRepository = hundeRepository;
    }


    @Override
    public Hund getHundById(int hundId) {
        return hundeRepository.findById(hundId).orElse(null);
    }

    @Override
    public List<Hund> getAllHunde() {
        return hundeRepository.findAll();
    }

    @Override
    public Hund deleteHundById(int hundId) {
        Hund hund = hundeRepository.findById(hundId).orElse(null);
        hundeRepository.delete(hund);
        return hund;
    }

    @Override
    public Hund addHund(String name, int age) {
        Hund neuerHund = new Hund(name, age);
        return hundeRepository.save(neuerHund);
    }
}
