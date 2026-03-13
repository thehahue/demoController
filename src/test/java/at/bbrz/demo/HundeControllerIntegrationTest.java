package at.bbrz.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HundeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getHundById_withId1_returnsFifi() throws Exception {
        mockMvc.perform(get("/dog/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Fifi"))
                .andExpect(jsonPath("$.age").value(5));
    }

    @Test
    void getHundById_withId2_returnsBello() throws Exception {
        mockMvc.perform(get("/dog/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Bello"))
                .andExpect(jsonPath("$.age").value(3));
    }

    @Test
    void getHundById_withId3_returnsLuna() throws Exception {
        mockMvc.perform(get("/dog/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("Luna"))
                .andExpect(jsonPath("$.age").value(7));
    }

    @Test
    void getHundById_withNonExistingId_returns404() throws Exception {
        mockMvc.perform(get("/dog/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllDogs_returnsAllDogs() throws Exception {
        mockMvc.perform(get("/allDogs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Fifi"))
                .andExpect(jsonPath("$[0].age").value(5))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Bello"))
                .andExpect(jsonPath("$[1].age").value(3))
                .andExpect(jsonPath("$[2].id").value(3))
                .andExpect(jsonPath("$[2].name").value("Luna"))
                .andExpect(jsonPath("$[2].age").value(7));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void deleteHundById_withExistingId_deletesHund() throws Exception {
        mockMvc.perform(delete("/dog/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Bello"))
                .andExpect(jsonPath("$.age").value(3));

        mockMvc.perform(get("/allDogs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(3));
    }

    @Test
    void deleteHundById_withNonExistingId_returns404() throws Exception {
        mockMvc.perform(delete("/dog/99"))
                .andExpect(status().isNotFound());
    }
}
