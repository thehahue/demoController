package at.bbrz.demo.wetten;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Convert;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "wettveranstaltungen")
public class Wettveranstaltung {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String bezeichnung;
    private String typ;

    @Column(name = "ergebnis_typ")
    @Convert(converter = ErgebnisMusterConverter.class)
    private ErgebnisMuster ergebnisMuster;

    private LocalDate datum;
    private LocalTime uhrzeit;

    @ElementCollection
    @CollectionTable(
            name = "wettveranstaltung_teilnehmer",
            joinColumns = @JoinColumn(name = "veranstaltung_id")
    )
    @Column(name = "teilnehmer")
    @OrderColumn(name = "teilnehmer_index")
    private List<String> teilnehmer = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "wettveranstaltung_wettquoten",
            joinColumns = @JoinColumn(name = "veranstaltung_id")
    )
    @OrderColumn(name = "quote_index")
    private List<Wettquote> wettquoten = new ArrayList<>();

    public Wettveranstaltung() {
    }

    public Wettveranstaltung(
            String bezeichnung,
            String typ,
            ErgebnisMuster ergebnisMuster,
            LocalDate datum,
            LocalTime uhrzeit,
            List<String> teilnehmer,
            List<Wettquote> wettquoten
    ) {
        this.bezeichnung = bezeichnung;
        this.typ = typ;
        this.ergebnisMuster = ergebnisMuster;
        this.datum = datum;
        this.uhrzeit = uhrzeit;
        this.teilnehmer = new ArrayList<>(teilnehmer);
        this.wettquoten = new ArrayList<>(wettquoten);
    }

    public Integer getId() {
        return id;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public String getTyp() {
        return typ;
    }

    public ErgebnisMuster getErgebnisMuster() {
        return ergebnisMuster;
    }

    public LocalDate getDatum() {
        return datum;
    }

    public LocalTime getUhrzeit() {
        return uhrzeit;
    }

    public List<String> getTeilnehmer() {
        return teilnehmer;
    }

    public List<Wettquote> getWettquoten() {
        return wettquoten;
    }
}
