package at.bbrz.demo.wetten;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class WettveranstaltungRequest {
    private String bezeichnung;
    private String typ;
    @JsonAlias("ergebnisTyp")
    private ErgebnisMuster ergebnisMuster;
    private LocalDate datum;
    private LocalTime uhrzeit;
    private List<String> teilnehmer;
    private List<Wettquote> wettquoten;

    public WettveranstaltungRequest() {
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public String getTyp() {
        return typ;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }

    public ErgebnisMuster getErgebnisMuster() {
        return ergebnisMuster;
    }

    public void setErgebnisMuster(ErgebnisMuster ergebnisMuster) {
        this.ergebnisMuster = ergebnisMuster;
    }

    public LocalDate getDatum() {
        return datum;
    }

    public void setDatum(LocalDate datum) {
        this.datum = datum;
    }

    public LocalTime getUhrzeit() {
        return uhrzeit;
    }

    public void setUhrzeit(LocalTime uhrzeit) {
        this.uhrzeit = uhrzeit;
    }

    public List<String> getTeilnehmer() {
        return teilnehmer;
    }

    public void setTeilnehmer(List<String> teilnehmer) {
        this.teilnehmer = teilnehmer;
    }

    public List<Wettquote> getWettquoten() {
        return wettquoten;
    }

    public void setWettquoten(List<Wettquote> wettquoten) {
        this.wettquoten = wettquoten;
    }
}
