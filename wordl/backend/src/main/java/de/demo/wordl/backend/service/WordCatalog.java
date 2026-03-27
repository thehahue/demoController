package de.demo.wordl.backend.service;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;
import org.springframework.stereotype.Component;

@Component
public class WordCatalog {

    private final Map<Integer, List<String>> wordsByLength;

    public WordCatalog() {
        this(defaultWords());
    }

    WordCatalog(Map<Integer, List<String>> wordsByLength) {
        this.wordsByLength = wordsByLength.entrySet().stream()
                .collect(java.util.stream.Collectors.toUnmodifiableMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .map(word -> word.toUpperCase(Locale.ROOT))
                                .toList()));
    }

    public boolean supports(int wordLength) {
        return wordsByLength.containsKey(wordLength);
    }

    public List<Integer> supportedLengths() {
        return new TreeSet<>(wordsByLength.keySet()).stream().toList();
    }

    public String randomWord(int wordLength, Random random) {
        List<String> words = wordsByLength.get(wordLength);
        if (words == null || words.isEmpty()) {
            throw new IllegalArgumentException("Unsupported word length: " + wordLength);
        }
        return words.get(random.nextInt(words.size()));
    }

    private static Map<Integer, List<String>> defaultWords() {
        return Map.of(
                4, List.of(
                        "BARK", "CALM", "DUNE", "ECHO", "FERN", "GLOW", "HAZE", "IRIS", "JADE", "KITE",
                        "LAMP", "MINT", "NOVA", "OATH", "PINE", "ROAM", "SAGE", "TIDE", "VAST", "WAVE"),
                5, List.of(
                        "APPLE", "BRICK", "CRANE", "DRAFT", "EMBER", "FLARE", "GHOST", "HONEY", "IVORY", "JOLLY",
                        "KNACK", "LEMON", "MANGO", "NERVE", "OCEAN", "PLANT", "QUILL", "RIVER", "STONE", "TRACE",
                        "ULTRA", "VIVID", "WAGON", "YEARN", "ZESTY"),
                6, List.of(
                        "ANCHOR", "BEACON", "CANDLE", "DRAGON", "ELIXIR", "FALCON", "GARDEN", "HARBOR", "ICICLE",
                        "JUNGLE", "KETTLE", "MARBLE", "NECTAR", "ORANGE", "POCKET", "QUARTZ", "ROCKET", "STREAM",
                        "THRIVE", "UPDATE", "VOYAGE", "YONDER"));
    }
}
