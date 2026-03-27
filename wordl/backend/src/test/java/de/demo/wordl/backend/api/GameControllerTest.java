package de.demo.wordl.backend.api;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.demo.wordl.backend.api.dto.GameStateResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createsGameLoadsGameAndAcceptsGuess() throws Exception {
        MvcResult createResult = mockMvc.perform(post("/api/games")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"wordLength":5}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.wordLength").value(5))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andReturn();

        GameStateResponse game = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                GameStateResponse.class);

        mockMvc.perform(get("/api/games/{gameId}", game.gameId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId").value(game.gameId().toString()))
                .andExpect(jsonPath("$.attemptCount").value(0));

        mockMvc.perform(post("/api/games/{gameId}/guesses", game.gameId())
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"guess":"crane"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.attemptCount").value(1))
                .andExpect(jsonPath("$.guesses[0].guess").value("CRANE"))
                .andExpect(jsonPath("$.guesses[0].letters.length()").value(5));
    }

    @Test
    void rejectsUnsupportedWordLength() throws Exception {
        mockMvc.perform(post("/api/games")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"wordLength":8}
                                """))
                .andExpect(status().isBadRequest());
    }
}
