package at.bbrz.demo;

import at.bbrz.demo.model.Hund;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

@Service
public class HundeService {
    private final Hashtable<Integer, Hund> hunde = new Hashtable<>();

    public HundeService() {
        hunde.put(1, new Hund("Fifi", 5, 1));
        hunde.put(2, new Hund("Bello", 3, 2));
        hunde.put(3, new Hund("Luna", 7, 3));
    }

    public Hund getHundById(int hundId) {
        return hunde.get(hundId);
    }

    public List<Hund> getAllHunde() {
        List<Hund> alleHunde = new ArrayList<>(hunde.values());
        alleHunde.sort(Comparator.comparingInt(Hund::getId));
        return alleHunde;
    }

    public Hund deleteHundById(int hundId) {
        return hunde.remove(hundId);
    }

    public Hund addHund(String name, int age) {
        int neueId = naechsteId();
        Hund neuerHund = new Hund(name, age, neueId);
        hunde.put(neueId, neuerHund);
        return neuerHund;
    }

    private int naechsteId() {
        int max = 0;
        for (int id : hunde.keySet()) {
            if (id > max) {
                max = id;
            }
        }
        return max + 1;
    }
}
