package at.bbrz.demo;

import org.springframework.stereotype.Service;

import java.util.Hashtable;

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

    public Hund addHund(int id, String name, int age) {
        if (hunde.containsKey(id)) {
            throw new IllegalArgumentException("Hund mit ID " + id + " existiert bereits.");
        }

        Hund neuerHund = new Hund(name, age, id);
        hunde.put(id, neuerHund);
        return neuerHund;
    }
}
