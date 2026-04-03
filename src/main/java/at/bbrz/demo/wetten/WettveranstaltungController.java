package at.bbrz.demo.wetten;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class WettveranstaltungController {
    private final WettveranstaltungService wettveranstaltungService;

    public WettveranstaltungController(WettveranstaltungService wettveranstaltungService) {
        this.wettveranstaltungService = wettveranstaltungService;
    }

    @PostMapping("/wetten/veranstaltungen")
    @ResponseStatus(HttpStatus.CREATED)
    public Wettveranstaltung createVeranstaltung(@RequestBody WettveranstaltungRequest request) {
        return wettveranstaltungService.createVeranstaltung(request);
    }

    @GetMapping("/wetten/veranstaltungen")
    public List<Wettveranstaltung> getAlleVeranstaltungen() {
        return wettveranstaltungService.getAlleVeranstaltungen();
    }
}
