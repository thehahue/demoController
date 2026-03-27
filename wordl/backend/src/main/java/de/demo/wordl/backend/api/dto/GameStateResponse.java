package de.demo.wordl.backend.api.dto;

import de.demo.wordl.backend.game.GameStatus;
import de.demo.wordl.backend.game.GuessEvaluation;
import java.util.List;
import java.util.UUID;

public record GameStateResponse(
        UUID gameId,
        int wordLength,
        int maxAttempts,
        int attemptCount,
        GameStatus status,
        List<GuessEvaluation> guesses,
        String message,
        String solution) {
}
