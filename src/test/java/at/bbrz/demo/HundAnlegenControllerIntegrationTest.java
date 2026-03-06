package at.bbrz.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class HundAnlegenControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createHund_withValidData_returns201() throws Exception {
        mockMvc.perform(post("/dog/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Rex\", \"age\": 2}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Rex"))
                .andExpect(jsonPath("$.age").value(2))
                .andExpect(jsonPath("$.id").value(4));
    }

    @Test
    void createHund_idWirdAutomatischVergeben_naechsteFreieId() throws Exception {
        // Erster neuer Hund bekommt ID 4 (nach Fifi=1, Bello=2, Luna=3)
        mockMvc.perform(post("/dog/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Rex\", \"age\": 2}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(4));

        // Zweiter neuer Hund bekommt ID 5
        mockMvc.perform(post("/dog/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Bruno\", \"age\": 4}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    void createHund_alterNull_returns400() throws Exception {
        mockMvc.perform(post("/dog/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Rex\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createHund_alterNegativ_returns400() throws Exception {
        mockMvc.perform(post("/dog/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Rex\", \"age\": -1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createHund_alterNull_nameFehlend_returns400() throws Exception {
        mockMvc.perform(post("/dog/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"age\": 3}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createHund_nameLeer_returns400() throws Exception {
        mockMvc.perform(post("/dog/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"\", \"age\": 2}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createHund_nameNurLeerzeichen_returns400() throws Exception {
        mockMvc.perform(post("/dog/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"   \", \"age\": 2}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createHund_alterNull_isGueltig() throws Exception {
        // Alter 0 ist erlaubt (>= 0)
        mockMvc.perform(post("/dog/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Welpe\", \"age\": 0}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.age").value(0));
    }
}
