package de.demo.wordl.backend.service;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WordCatalog {

    private static final Logger LOGGER = LoggerFactory.getLogger(WordCatalog.class);

    private final Map<Integer, List<String>> defaultWordsByLength;
    private final WordPluginLoader wordPluginLoader;
    private volatile Map<Integer, List<String>> pluginWordsByLength = Map.of();
    // The scheduler replaces this snapshot while request threads read it. volatile publishes each new map reference
    // safely, so readers either see the old complete catalog or the new complete catalog.
    private volatile Map<Integer, List<String>> wordsByLength;

    @Autowired
    public WordCatalog(WordPluginLoader wordPluginLoader) {
        this(defaultWords(), wordPluginLoader);
    }

    WordCatalog(Map<Integer, List<String>> wordsByLength) {
        this(wordsByLength, Map.of());
    }

    WordCatalog(Map<Integer, List<String>> defaultWordsByLength, Map<Integer, List<String>> pluginWordsByLength) {
        this.defaultWordsByLength = normalizeWordsByLength(defaultWordsByLength);
        this.wordPluginLoader = null;
        replacePluginWords(pluginWordsByLength);
    }

    private WordCatalog(Map<Integer, List<String>> defaultWordsByLength, WordPluginLoader wordPluginLoader) {
        this.defaultWordsByLength = normalizeWordsByLength(defaultWordsByLength);
        this.wordPluginLoader = wordPluginLoader;
        replacePluginWords(wordPluginLoader.loadPluginWords());
    }

    @Scheduled(initialDelay = 10_000, fixedDelay = 10_000)
    public void refreshPluginWords() {
        if (wordPluginLoader == null) {
            return;
        }
        replacePluginWords(wordPluginLoader.loadPluginWords());
    }

    void replacePluginWords(Map<Integer, List<String>> pluginWordsByLength) {
        Map<Integer, List<String>> normalizedPluginWordsByLength = normalizeWordsByLength(pluginWordsByLength);
        logPluginChanges(this.pluginWordsByLength, normalizedPluginWordsByLength);

        Map<Integer, List<String>> mergedWordsByLength = new LinkedHashMap<>();
        this.defaultWordsByLength.forEach((wordLength, words) ->
                mergedWordsByLength.computeIfAbsent(wordLength, ignored -> new java.util.ArrayList<>()).addAll(words));
        normalizedPluginWordsByLength.forEach((wordLength, words) ->
                mergedWordsByLength.computeIfAbsent(wordLength, ignored -> new java.util.ArrayList<>()).addAll(words));

        // Build and normalize the replacement completely in local variables, then swap the volatile reference once.
        // This keeps catalog refreshes atomic from the perspective of concurrent game requests.
        this.wordsByLength = mergedWordsByLength.entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), normalizeWords(entry.getKey(), entry.getValue())))
                .filter(entry -> !entry.getValue().isEmpty())
                .collect(java.util.stream.Collectors.toUnmodifiableMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue));
        this.pluginWordsByLength = normalizedPluginWordsByLength;
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

    private static Map<Integer, List<String>> normalizeWordsByLength(Map<Integer, List<String>> wordsByLength) {
        return wordsByLength.entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), normalizeWords(entry.getKey(), entry.getValue())))
                .filter(entry -> !entry.getValue().isEmpty())
                .collect(java.util.stream.Collectors.toUnmodifiableMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue));
    }

    private static void logPluginChanges(
            Map<Integer, List<String>> previousPluginWordsByLength,
            Map<Integer, List<String>> nextPluginWordsByLength) {
        nextPluginWordsByLength.forEach((wordLength, words) -> {
            List<String> previousWords = previousPluginWordsByLength.get(wordLength);
            if (previousWords == null) {
                LOGGER.info("Loaded WORDL word plugin catalog for {} letters with {}.",
                        wordLength, wordEntryCount(words.size()));
            } else if (!previousWords.equals(words)) {
                LOGGER.info("Reloaded WORDL word plugin catalog for {} letters: {} -> {}.",
                        wordLength, wordEntryCount(previousWords.size()), wordEntryCount(words.size()));
            }
        });

        previousPluginWordsByLength.forEach((wordLength, words) -> {
            if (!nextPluginWordsByLength.containsKey(wordLength)) {
                LOGGER.info("Unloaded WORDL word plugin catalog for {} letters with {}.",
                        wordLength, wordEntryCount(words.size()));
            }
        });
    }

    private static String wordEntryCount(int count) {
        return count == 1 ? "1 word entry" : count + " word entries";
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
