package at.bbrz.demo;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
public class HundeController {
    private List<Hund> hunde=new ArrayList<>();

    public HundeController() {
        // Demo-Daten
        hunde.add(new Hund("Fifi", 5, 1));
        hunde.add(new Hund("Bello",3, 2));
        hunde.add(new Hund("Luna",7, 3));
    }

    @GetMapping("/dog/{hundId}")
    public Hund getHundById(@PathVariable int hundId) {
        for (Hund hund : hunde) {
            if (hund.getId() == hundId) {
                return hund;
            }
        }

        throw new ResponseStatusException
                (HttpStatus.NOT_FOUND, "Hund mit ID: "+hundId+" nicht gefunden");
    }
}
