package at.bbrz.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:wetten-test;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class WettveranstaltungControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createVeranstaltung_withValidData_returns201() throws Exception {
        mockMvc.perform(post("/wetten/veranstaltungen")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "bezeichnung": "Champions League Finale",
                                  "typ": "Fussball",
                                  "ergebnisMuster": "ERGEBNISSTAND",
                                  "datum": "2026-06-12",
                                  "uhrzeit": "20:45",
                                  "teilnehmer": ["FC Nordstadt", "SV Suedstadt"],
                                  "wettquoten": [
                                    {"gewinn": 1.85, "verlust": 2.10, "unentschieden": 3.20},
                                    {"gewinn": 2.30, "verlust": 1.70, "unentschieden": 3.05}
                                  ]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.bezeichnung").value("Champions League Finale"))
                .andExpect(jsonPath("$.typ").value("Fussball"))
                .andExpect(jsonPath("$.ergebnisMuster").value("X:X"))
                .andExpect(jsonPath("$.datum").value("2026-06-12"))
                .andExpect(jsonPath("$.uhrzeit").value("20:45:00"))
                .andExpect(jsonPath("$.teilnehmer[0]").value("FC Nordstadt"))
                .andExpect(jsonPath("$.teilnehmer[1]").value("SV Suedstadt"))
                .andExpect(jsonPath("$.wettquoten[0].gewinn").value(1.85))
                .andExpect(jsonPath("$.wettquoten[0].verlust").value(2.10))
                .andExpect(jsonPath("$.wettquoten[0].unentschieden").value(3.20))
                .andExpect(jsonPath("$.wettquoten[1].gewinn").value(2.30));
    }

    @Test
    void getAlleVeranstaltungen_returnsCreatedEventsSorted() throws Exception {
        mockMvc.perform(post("/wetten/veranstaltungen")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "bezeichnung": "Boxnacht Wien",
                                  "typ": "Boxen",
                                  "ergebnisMuster": "KO_RUNDE",
                                  "datum": "2026-07-01",
                                  "uhrzeit": "21:00",
                                  "teilnehmer": ["Kampf A", "Kampf B"],
                                  "wettquoten": [
                                    {"gewinn": 1.60, "verlust": 2.30, "unentschieden": 4.10},
                                    {"gewinn": 2.05, "verlust": 1.75, "unentschieden": 3.50}
                                  ]
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/wetten/veranstaltungen")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "bezeichnung": "Tischtennis Cup",
                                  "typ": "Tischtennis",
                                  "ergebnisMuster": "SATZERGEBNIS",
                                  "datum": "2026-06-10",
                                  "uhrzeit": "18:30",
                                  "teilnehmer": ["Spieler 1", "Spieler 2"],
                                  "wettquoten": [
                                    {"gewinn": 1.95, "verlust": 1.95, "unentschieden": 2.80},
                                    {"gewinn": 1.90, "verlust": 2.00, "unentschieden": 2.95}
                                  ]
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/wetten/veranstaltungen"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].bezeichnung").value("Tischtennis Cup"))
                .andExpect(jsonPath("$[0].typ").value("Tischtennis"))
                .andExpect(jsonPath("$[1].bezeichnung").value("Boxnacht Wien"))
                .andExpect(jsonPath("$[1].typ").value("Boxen"));
    }

    @Test
    void createVeranstaltung_withDifferentListSizes_returns400() throws Exception {
        mockMvc.perform(post("/wetten/veranstaltungen")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "bezeichnung": "Fehlkonfiguration",
                                  "typ": "Fussball",
                                  "ergebnisMuster": "ERGEBNISSTAND",
                                  "datum": "2026-06-12",
                                  "uhrzeit": "20:45",
                                  "teilnehmer": ["Team A", "Team B"],
                                  "wettquoten": [
                                    {"gewinn": 1.85, "verlust": 2.10, "unentschieden": 3.20}
                                  ]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Teilnehmer und Wettquoten muessen gleich viele Eintraege haben."))
                .andExpect(jsonPath("$.path").value("/wetten/veranstaltungen"));
    }

    @Test
    void createVeranstaltung_withInvalidNumberFormat_returns400WithReadableMessage() throws Exception {
        mockMvc.perform(post("/wetten/veranstaltungen")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "bezeichnung": "Fehlerhafte Quote",
                                  "typ": "Fussball",
                                  "ergebnisMuster": "ERGEBNISSTAND",
                                  "datum": "2026-06-12",
                                  "uhrzeit": "20:45",
                                  "teilnehmer": ["Team A", "Team B"],
                                  "wettquoten": [
                                    {"gewinn": "abc", "verlust": 2.10, "unentschieden": 3.20},
                                    {"gewinn": 1.95, "verlust": 2.05, "unentschieden": 2.80}
                                  ]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Ungueltiges JSON oder unpassendes Datums-, Uhrzeit-, Zahlen- oder Enum-Format."))
                .andExpect(jsonPath("$.path").value("/wetten/veranstaltungen"));
    }

    @Test
    void createVeranstaltung_withMissingUnentschiedenQuote_returns400() throws Exception {
        mockMvc.perform(post("/wetten/veranstaltungen")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "bezeichnung": "Fehlende Quote",
                                  "typ": "Fussball",
                                  "ergebnisMuster": "ERGEBNISSTAND",
                                  "datum": "2026-06-12",
                                  "uhrzeit": "20:45",
                                  "teilnehmer": ["Team A"],
                                  "wettquoten": [
                                    {"gewinn": 1.85, "verlust": 2.10, "unentschieden": 0}
                                  ]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Unentschieden-Quote muss groesser als 0 sein."))
                .andExpect(jsonPath("$.path").value("/wetten/veranstaltungen"));
    }

    @Test
    void createVeranstaltung_withLegacyErgebnisTypField_isStillAccepted() throws Exception {
        mockMvc.perform(post("/wetten/veranstaltungen")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "bezeichnung": "Legacy Request",
                                  "typ": "Fussball",
                                  "ergebnisTyp": "X:X",
                                  "datum": "2026-06-12",
                                  "uhrzeit": "20:45",
                                  "teilnehmer": ["Team A"],
                                  "wettquoten": [
                                    {"gewinn": 1.85, "verlust": 2.10, "unentschieden": 3.20}
                                  ]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ergebnisMuster").value("X:X"));
    }

    @Test
    void createVeranstaltung_withInvalidErgebnisMuster_returns400() throws Exception {
        mockMvc.perform(post("/wetten/veranstaltungen")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "bezeichnung": "Ungueltiges Muster",
                                  "typ": "Fussball",
                                  "ergebnisMuster": "FREITEXT",
                                  "datum": "2026-06-12",
                                  "uhrzeit": "20:45",
                                  "teilnehmer": ["Team A"],
                                  "wettquoten": [
                                    {"gewinn": 1.85, "verlust": 2.10, "unentschieden": 3.20}
                                  ]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Ungueltiges JSON oder unpassendes Datums-, Uhrzeit-, Zahlen- oder Enum-Format."))
                .andExpect(jsonPath("$.path").value("/wetten/veranstaltungen"));
    }
}
