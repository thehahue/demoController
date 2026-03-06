package at.bbrz.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class HundAnlegenController {
    private final HundeService hundeService;

    @Autowired
    public HundAnlegenController(HundeService hundeService) {
        this.hundeService = hundeService;
    }

    @PostMapping("/dog/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Hund createHund(@RequestBody HundAnlegenRequest request) {
        validateRequest(request);

        try {
            return hundeService.addHund(
                    request.getId(),
                    request.getName().trim(),
                    request.getAge()
            );
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
        }
    }

    private void validateRequest(HundAnlegenRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request darf nicht leer sein.");
        }
        if (request.getId() == null || request.getId() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID muss groesser als 0 sein.");
        }
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name darf nicht leer sein.");
        }
        if (request.getAge() == null || request.getAge() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Alter muss 0 oder groesser sein.");
        }
    }
}
