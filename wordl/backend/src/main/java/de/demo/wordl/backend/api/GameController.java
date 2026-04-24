package de.demo.wordl.backend.api;

import de.demo.wordl.backend.api.dto.CreateGameRequest;
import de.demo.wordl.backend.api.dto.GameStateResponse;
import de.demo.wordl.backend.api.dto.GuessRequest;
import de.demo.wordl.backend.service.GameService;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping
    public GameStateResponse createGame(@RequestBody(required = false) CreateGameRequest request) {
        return gameService.createGame(request == null ? new CreateGameRequest(null) : request);
    }

    @GetMapping("/word-lengths")
    public List<Integer> supportedWordLengths() {
        return gameService.supportedWordLengths();
    }

    @GetMapping("/{gameId}")
    public GameStateResponse getGame(@PathVariable UUID gameId) {
        return gameService.getGame(gameId);
    }

    @PostMapping("/{gameId}/guesses")
    public GameStateResponse submitGuess(@PathVariable UUID gameId, @RequestBody GuessRequest request) {
        return gameService.submitGuess(gameId, request);
    }
}
