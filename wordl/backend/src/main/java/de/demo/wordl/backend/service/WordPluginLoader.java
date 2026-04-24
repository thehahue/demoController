package de.demo.wordl.backend.service;

import de.demo.wordl.backend.plugin.WordListPlugin;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WordPluginLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(WordPluginLoader.class);
    private static final String PLUGIN_DIR_PROPERTY = "wordl.plugin.dir";
    private static final String PLUGIN_DIR_ENV = "WORDL_PLUGIN_DIR";

    public Map<Integer, List<String>> loadPluginWords() {
        List<Path> pluginJars = findPluginJars();
        if (pluginJars.isEmpty()) {
            LOGGER.info("No WORDL word plugin JARs found.");
            return Map.of();
        }

        List<URL> pluginUrls = new ArrayList<>();
        for (Path pluginJar : pluginJars) {
            try {
                pluginUrls.add(pluginJar.toUri().toURL());
            } catch (IOException exception) {
                LOGGER.warn("Skipping WORDL word plugin JAR {} because its URL cannot be created.", pluginJar, exception);
            }
        }

        if (pluginUrls.isEmpty()) {
            return Map.of();
        }

        Map<Integer, List<String>> wordsByType = new LinkedHashMap<>();
        try (URLClassLoader pluginClassLoader = new URLClassLoader(
                pluginUrls.toArray(URL[]::new),
                WordListPlugin.class.getClassLoader())) {
            ServiceLoader<WordListPlugin> plugins = ServiceLoader.load(WordListPlugin.class, pluginClassLoader);
            plugins.stream().forEach(pluginProvider -> registerPluginProviderWords(wordsByType, pluginProvider));
        } catch (IOException exception) {
            LOGGER.warn("Could not close WORDL word plugin class loader cleanly.", exception);
        }

        return wordsByType.entrySet().stream()
                .collect(java.util.stream.Collectors.toUnmodifiableMap(
                        Map.Entry::getKey,
                        entry -> List.copyOf(entry.getValue())));
    }

    private void registerPluginProviderWords(
            Map<Integer, List<String>> wordsByType,
            ServiceLoader.Provider<WordListPlugin> pluginProvider) {
        String pluginName = "unknown";
        try {
            pluginName = pluginProvider.type().getName();
            registerPluginWords(wordsByType, pluginProvider.get());
        } catch (ServiceConfigurationError | RuntimeException exception) {
            LOGGER.warn("Ignoring WORDL word plugin provider {} because it could not be loaded.",
                    pluginName, exception);
        }
    }

    private void registerPluginWords(Map<Integer, List<String>> wordsByType, WordListPlugin plugin) {
        int type = plugin.type();
        if (type <= 0) {
            LOGGER.warn("Ignoring WORDL word plugin {} because type {} is not a valid word length.",
                    plugin.getClass().getName(), type);
            return;
        }

        List<String> words = plugin.words();
        if (words == null || words.isEmpty()) {
            LOGGER.warn("Ignoring WORDL word plugin {} for type {} because it provides no words.",
                    plugin.getClass().getName(), type);
            return;
        }

        wordsByType.computeIfAbsent(type, ignored -> new ArrayList<>()).addAll(words);
        LOGGER.info("Loaded WORDL word plugin {} for {} letters with {} words.",
                plugin.getClass().getName(), type, words.size());
    }

    private List<Path> findPluginJars() {
        Set<Path> pluginDirectories = new LinkedHashSet<>();
        configuredPluginDirectory().ifPresent(pluginDirectories::add);
        applicationDirectory().ifPresent(pluginDirectories::add);
        pluginDirectories.add(Path.of("").toAbsolutePath().normalize());

        Set<Path> pluginJars = new LinkedHashSet<>();
        for (Path pluginDirectory : pluginDirectories) {
            if (!Files.isDirectory(pluginDirectory)) {
                continue;
            }
            try (var files = Files.list(pluginDirectory)) {
                files.filter(Files::isRegularFile)
                        .filter(path -> path.getFileName().toString().endsWith(".jar"))
                        .map(path -> path.toAbsolutePath().normalize())
                        .forEach(pluginJars::add);
            } catch (IOException exception) {
                LOGGER.warn("Could not scan WORDL plugin directory {}.", pluginDirectory, exception);
            }
        }
        return List.copyOf(pluginJars);
    }

    private java.util.Optional<Path> configuredPluginDirectory() {
        String configuredDirectory = System.getProperty(PLUGIN_DIR_PROPERTY);
        if (configuredDirectory == null || configuredDirectory.isBlank()) {
            configuredDirectory = System.getenv(PLUGIN_DIR_ENV);
        }
        if (configuredDirectory == null || configuredDirectory.isBlank()) {
            return java.util.Optional.empty();
        }
        return java.util.Optional.of(Path.of(configuredDirectory).toAbsolutePath().normalize());
    }

    private java.util.Optional<Path> applicationDirectory() {
        try {
            URL location = WordPluginLoader.class.getProtectionDomain().getCodeSource().getLocation();
            Path applicationPath = Path.of(location.toURI()).toAbsolutePath().normalize();
            if (Files.isRegularFile(applicationPath)) {
                return java.util.Optional.ofNullable(applicationPath.getParent());
            }
            return java.util.Optional.of(applicationPath);
        } catch (URISyntaxException | RuntimeException exception) {
            LOGGER.warn("Could not determine WORDL application directory.", exception);
            return java.util.Optional.empty();
        }
    }
}
