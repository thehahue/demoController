package at.bbrz.demo.service;

import at.bbrz.demo.model.Hund;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HundePermService implements HundeServiceInterface {
    public HundePermService() {
    }

    @Override
    public Hund getHundById(int hundId) {
        return null;
    }

    @Override
    public List<Hund> getAllHunde() {
        return List.of();
    }

    @Override
    public Hund deleteHundById(int hundId) {
        return null;
    }

    @Override
    public Hund addHund(String name, int age) {
        return null;
    }
}
