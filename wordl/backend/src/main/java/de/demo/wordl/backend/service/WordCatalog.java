package de.demo.wordl.backend.service;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.TreeSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WordCatalog {

    private final Map<Integer, List<String>> wordsByLength;

    @Autowired
    public WordCatalog(WordPluginLoader wordPluginLoader) {
        this(defaultWords(), wordPluginLoader.loadPluginWords());
    }

    WordCatalog(Map<Integer, List<String>> wordsByLength) {
        this(wordsByLength, Map.of());
    }

    WordCatalog(Map<Integer, List<String>> defaultWordsByLength, Map<Integer, List<String>> pluginWordsByLength) {
        Map<Integer, List<String>> mergedWordsByLength = new LinkedHashMap<>();
        defaultWordsByLength.forEach((wordLength, words) ->
                mergedWordsByLength.computeIfAbsent(wordLength, ignored -> new java.util.ArrayList<>()).addAll(words));
        pluginWordsByLength.forEach((wordLength, words) ->
                mergedWordsByLength.computeIfAbsent(wordLength, ignored -> new java.util.ArrayList<>()).addAll(words));

        this.wordsByLength = mergedWordsByLength.entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), normalizeWords(entry.getKey(), entry.getValue())))
                .filter(entry -> !entry.getValue().isEmpty())
                .collect(java.util.stream.Collectors.toUnmodifiableMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue));
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

    private static List<String> normalizeWords(int wordLength, List<String> words) {
        return words.stream()
                .filter(Objects::nonNull)
                .map(word -> word.toUpperCase(Locale.ROOT))
                .filter(word -> word.length() == wordLength)
                .filter(word -> word.matches("[A-Z]+"))
                .collect(java.util.stream.Collectors.collectingAndThen(
                        java.util.stream.Collectors.toCollection(LinkedHashSet::new),
                        List::copyOf));
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
