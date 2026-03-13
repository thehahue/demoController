package at.bbrz.demo.controller;

import at.bbrz.demo.model.Hund;
import at.bbrz.demo.model.HundAnlegenRequest;
import at.bbrz.demo.service.HundeServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class HundAnlegenController {
    private final HundeServiceInterface hundeService;

    @Autowired
    public HundAnlegenController(HundeServiceInterface hundeService) {
        this.hundeService = hundeService;
    }

    @PostMapping("/dog/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Hund createHund(@RequestBody HundAnlegenRequest request) {
        validateRequest(request);

        return hundeService.addHund(
                request.getName().trim(),
                request.getAge()
        );
    }

    private void validateRequest(HundAnlegenRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request darf nicht leer sein.");
        }
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name darf nicht leer sein.");
        }
        if (request.getAge() == null || request.getAge() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Alter muss 0 oder groesser sein.");
        }
    }
}
