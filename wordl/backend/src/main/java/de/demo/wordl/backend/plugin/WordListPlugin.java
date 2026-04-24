package de.demo.wordl.backend.plugin;

import java.util.List;

/**
 * Service Provider Interface for external WORDL word list plugins.
 */
public interface WordListPlugin {

    /**
     * The word length provided by this plugin, for example {@code 7} for seven-letter words.
     */
    int type();

    /**
     * Words that can be used as solutions for the configured word length.
     */
    List<String> words();
}
