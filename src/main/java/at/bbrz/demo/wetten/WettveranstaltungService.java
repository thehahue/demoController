package at.bbrz.demo.wetten;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class WettveranstaltungService {
    private final WettveranstaltungRepository wettveranstaltungRepository;

    public WettveranstaltungService(WettveranstaltungRepository wettveranstaltungRepository) {
        this.wettveranstaltungRepository = wettveranstaltungRepository;
    }

    public Wettveranstaltung createVeranstaltung(WettveranstaltungRequest request) {
        validateRequest(request);

        Wettveranstaltung veranstaltung = new Wettveranstaltung(
                request.getBezeichnung().trim(),
                request.getTyp().trim(),
                request.getErgebnisMuster(),
                request.getDatum(),
                request.getUhrzeit(),
                request.getTeilnehmer().stream().map(String::trim).toList(),
                request.getWettquoten().stream()
                        .map(quote -> new Wettquote(
                                quote.getGewinn(),
                                quote.getVerlust(),
                                quote.getUnentschieden()
                        ))
                        .toList()
        );

        return wettveranstaltungRepository.save(veranstaltung);
    }

    public List<Wettveranstaltung> getAlleVeranstaltungen() {
        return wettveranstaltungRepository.findAllByOrderByDatumAscUhrzeitAscIdAsc();
    }

    private void validateRequest(WettveranstaltungRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request darf nicht leer sein.");
        }
        if (isBlank(request.getBezeichnung())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bezeichnung darf nicht leer sein.");
        }
        if (isBlank(request.getTyp())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Typ darf nicht leer sein.");
        }
        if (request.getErgebnisMuster() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ergebnismuster muss ein gueltiger Enum-Wert sein.");
        }
        if (request.getDatum() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Datum darf nicht leer sein.");
        }
        if (request.getUhrzeit() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Uhrzeit darf nicht leer sein.");
        }
        if (request.getTeilnehmer() == null || request.getTeilnehmer().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mindestens ein Teilnehmer ist erforderlich.");
        }
        if (request.getWettquoten() == null || request.getWettquoten().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mindestens eine Wettquote ist erforderlich.");
        }
        if (request.getTeilnehmer().size() != request.getWettquoten().size()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Teilnehmer und Wettquoten muessen gleich viele Eintraege haben."
            );
        }

        for (String teilnehmer : request.getTeilnehmer()) {
            if (isBlank(teilnehmer)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Teilnehmer duerfen keine leeren Eintraege enthalten."
                );
            }
        }

        for (Wettquote quote : request.getWettquoten()) {
            if (quote == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Jeder Teilnehmer braucht drei Quoten: Gewinn, Verlust und Unentschieden."
                );
            }
            validateEinzelquote(quote);
        }
    }

    private void validateEinzelquote(Wettquote quote) {
        if (quote.getGewinn() == null || quote.getGewinn().signum() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Gewinn-Quote muss groesser als 0 sein.");
        }
        if (quote.getVerlust() == null || quote.getVerlust().signum() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Verlust-Quote muss groesser als 0 sein.");
        }
        if (quote.getUnentschieden() == null || quote.getUnentschieden().signum() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unentschieden-Quote muss groesser als 0 sein.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
