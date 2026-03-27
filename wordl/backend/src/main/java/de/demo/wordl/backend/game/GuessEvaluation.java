package de.demo.wordl.backend.game;

import java.util.List;

public record GuessEvaluation(String guess, List<LetterEvaluation> letters) {
}
