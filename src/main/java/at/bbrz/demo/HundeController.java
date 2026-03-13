package at.bbrz.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class HundeController {
    private final HundeService hundeService;

    @Autowired
    public HundeController(HundeService hundeService) {
        this.hundeService = hundeService;
    }

    @GetMapping("/dog/{hundId}")
    public Hund getHundById(@PathVariable int hundId) {
        Hund hund = hundeService.getHundById(hundId);
        if (hund != null) {
            return hund;
        }

        throw new ResponseStatusException
                (HttpStatus.NOT_FOUND, "Hund mit ID: " + hundId + " nicht gefunden");
    }

    @GetMapping("/allDogs")
    public List<Hund> getAllDogs() {
        return hundeService.getAllHunde();
    }
}
