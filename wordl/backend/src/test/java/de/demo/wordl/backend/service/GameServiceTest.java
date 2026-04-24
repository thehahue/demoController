package de.demo.wordl.backend.service;

import static org.assertj.core.api.Assertions.assertThat;

import de.demo.wordl.backend.api.dto.CreateGameRequest;
import de.demo.wordl.backend.api.dto.GameStateResponse;
import de.demo.wordl.backend.api.dto.GuessRequest;
import de.demo.wordl.backend.game.GameStatus;
import de.demo.wordl.backend.game.LetterState;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.junit.jupiter.api.Test;

class GameServiceTest {

    @Test
    void marksDuplicateLettersLikeWordle() {
        GameService service = new GameService(
                new WordCatalog(Map.of(5, List.of("APPLE"))),
                new Random(0));

        GameStateResponse game = service.createGame(new CreateGameRequest(5));
        GameStateResponse updatedGame = service.submitGuess(game.gameId(), new GuessRequest("alley"));

        assertThat(updatedGame.attemptCount()).isEqualTo(1);
        assertThat(updatedGame.status()).isEqualTo(GameStatus.IN_PROGRESS);
        assertThat(updatedGame.guesses().get(0).letters())
                .extracting(letter -> letter.state())
                .containsExactly(
                        LetterState.CORRECT,
                        LetterState.PRESENT,
                        LetterState.ABSENT,
                        LetterState.PRESENT,
                        LetterState.ABSENT);
    }

    @Test
    void revealsSolutionAfterLastAttempt() {
        GameService service = new GameService(
                new WordCatalog(Map.of(5, List.of("APPLE"))),
                new Random(0));

        GameStateResponse game = service.createGame(new CreateGameRequest(5));
        for (int attempt = 0; attempt < 6; attempt++) {
            game = service.submitGuess(game.gameId(), new GuessRequest("brick"));
        }

        assertThat(game.status()).isEqualTo(GameStatus.LOST);
        assertThat(game.solution()).isEqualTo("APPLE");
    }

    @Test
    void usesPluginWordsForAdditionalWordLengths() {
        WordCatalog wordCatalog = new WordCatalog(
                Map.of(5, List.of("APPLE")),
                Map.of(7, List.of("balance", "too-short", "CAPTURE")));
        GameService service = new GameService(wordCatalog, new Random(0));

        GameStateResponse game = service.createGame(new CreateGameRequest(7));

        assertThat(game.wordLength()).isEqualTo(7);
        assertThat(wordCatalog.supportedLengths()).containsExactly(5, 7);
    }
}
