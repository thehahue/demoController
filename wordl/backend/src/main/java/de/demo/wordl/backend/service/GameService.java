package de.demo.wordl.backend.service;

import de.demo.wordl.backend.api.dto.CreateGameRequest;
import de.demo.wordl.backend.api.dto.GameStateResponse;
import de.demo.wordl.backend.api.dto.GuessRequest;
import de.demo.wordl.backend.game.GameStatus;
import de.demo.wordl.backend.game.GuessEvaluation;
import de.demo.wordl.backend.game.LetterEvaluation;
import de.demo.wordl.backend.game.LetterState;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class GameService {

    private static final int DEFAULT_WORD_LENGTH = 5;
    private static final int MAX_ATTEMPTS = 6;

    private final WordCatalog wordCatalog;
    private final Random random;
    private final Map<UUID, GameSession> sessions = new ConcurrentHashMap<>();

    @Autowired
    public GameService(WordCatalog wordCatalog) {
        this(wordCatalog, new SecureRandom());
    }

    GameService(WordCatalog wordCatalog, Random random) {
        this.wordCatalog = wordCatalog;
        this.random = random;
    }

    public GameStateResponse createGame(CreateGameRequest request) {
        int wordLength = normalizeWordLength(request.wordLength());
        String solution = wordCatalog.randomWord(wordLength, random);
        GameSession session = new GameSession(UUID.randomUUID(), solution, MAX_ATTEMPTS);
        sessions.put(session.gameId(), session);
        return toResponse(session, "New game started.");
    }

    public GameStateResponse getGame(UUID gameId) {
        return toResponse(getSession(gameId), "Game loaded.");
    }

    public GameStateResponse submitGuess(UUID gameId, GuessRequest request) {
        if (request == null || request.guess() == null || request.guess().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Guess must not be empty.");
        }

        GameSession session = getSession(gameId);
        if (session.status() != GameStatus.IN_PROGRESS) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Game is already finished.");
        }

        String guess = normalizeGuess(request.guess());
        if (guess.length() != session.wordLength()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Guess must contain exactly " + session.wordLength() + " letters.");
        }

        GuessEvaluation evaluation = evaluateGuess(guess, session.solution());
        session.guesses().add(evaluation);

        if (guess.equals(session.solution())) {
            session.status(GameStatus.WON);
            return toResponse(session, "Solved in " + session.guesses().size() + "/" + session.maxAttempts() + " attempts.");
        }

        if (session.guesses().size() >= session.maxAttempts()) {
            session.status(GameStatus.LOST);
            return toResponse(session, "No attempts left. The solution was " + session.solution() + ".");
        }

        return toResponse(
                session,
                "Attempt " + session.guesses().size() + " of " + session.maxAttempts() + " recorded.");
    }

    GuessEvaluation evaluateGuess(String guess, String solution) {
        List<LetterEvaluation> letters = new ArrayList<>(guess.length());
        for (int index = 0; index < guess.length(); index++) {
            letters.add(new LetterEvaluation(String.valueOf(guess.charAt(index)), LetterState.ABSENT));
        }

        Map<Character, Integer> remainingCharacters = new HashMap<>();
        for (int index = 0; index < solution.length(); index++) {
            char solutionLetter = solution.charAt(index);
            char guessLetter = guess.charAt(index);
            if (solutionLetter == guessLetter) {
                letters.set(index, new LetterEvaluation(String.valueOf(guessLetter), LetterState.CORRECT));
            } else {
                remainingCharacters.merge(solutionLetter, 1, Integer::sum);
            }
        }

        for (int index = 0; index < guess.length(); index++) {
            LetterEvaluation current = letters.get(index);
            if (current.state() == LetterState.CORRECT) {
                continue;
            }

            char guessLetter = guess.charAt(index);
            int remaining = remainingCharacters.getOrDefault(guessLetter, 0);
            if (remaining > 0) {
                letters.set(index, new LetterEvaluation(String.valueOf(guessLetter), LetterState.PRESENT));
                remainingCharacters.put(guessLetter, remaining - 1);
            }
        }

        return new GuessEvaluation(guess, List.copyOf(letters));
    }

    private GameSession getSession(UUID gameId) {
        GameSession session = sessions.get(gameId);
        if (session == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found.");
        }
        return session;
    }

    private int normalizeWordLength(Integer requestedWordLength) {
        int wordLength = requestedWordLength == null ? DEFAULT_WORD_LENGTH : requestedWordLength;
        if (!wordCatalog.supports(wordLength)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Unsupported word length. Supported lengths are " + wordCatalog.supportedLengths() + ".");
        }
        return wordLength;
    }

    private String normalizeGuess(String guess) {
        String normalized = guess.trim().toUpperCase(Locale.ROOT);
        if (!normalized.matches("[A-Z]+")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Guess must contain letters only.");
        }
        return normalized;
    }

    private GameStateResponse toResponse(GameSession session, String message) {
        String solution = session.status() == GameStatus.IN_PROGRESS ? null : session.solution();
        return new GameStateResponse(
                session.gameId(),
                session.wordLength(),
                session.maxAttempts(),
                session.guesses().size(),
                session.status(),
                List.copyOf(session.guesses()),
                message,
                solution);
    }

    private static final class GameSession {

        private final UUID gameId;
        private final String solution;
        private final int maxAttempts;
        private final List<GuessEvaluation> guesses = new ArrayList<>();
        private GameStatus status = GameStatus.IN_PROGRESS;

        private GameSession(UUID gameId, String solution, int maxAttempts) {
            this.gameId = gameId;
            this.solution = solution;
            this.maxAttempts = maxAttempts;
        }

        private UUID gameId() {
            return gameId;
        }

        private String solution() {
            return solution;
        }

        private int wordLength() {
            return solution.length();
        }

        private int maxAttempts() {
            return maxAttempts;
        }

        private List<GuessEvaluation> guesses() {
            return guesses;
        }

        private GameStatus status() {
            return status;
        }

        private void status(GameStatus status) {
            this.status = status;
        }
    }
}
