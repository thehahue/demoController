package at.bbrz.demo;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class HundeController {
    private Map<Integer, Hund> hunde=new HashMap<>();

    public HundeController() {
        // Demo-Daten
        hunde.put(1, new Hund("Fifi", 5, 1));
        hunde.put(2, new Hund("Bello",3, 2));
        hunde.put(3, new Hund("Luna",7, 3));
    }

    @GetMapping("/dog/{hundId}")
    public Hund getHundById(@PathVariable int hundId) {
        if (hunde.containsKey(hundId)) {
            return hunde.get(hundId);
        }

        throw new ResponseStatusException
                (HttpStatus.NOT_FOUND, "Hund mit ID: "+hundId+" nicht gefunden");
    }
}
