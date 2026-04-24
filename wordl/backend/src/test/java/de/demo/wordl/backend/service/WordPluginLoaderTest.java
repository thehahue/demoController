package de.demo.wordl.backend.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class WordPluginLoaderTest {

    @TempDir
    private Path tempDir;

    @Test
    void loadsWordPluginsFromConfiguredPluginDirectory() throws Exception {
        Path pluginJar = tempDir.resolve("seven-letter-words.jar");
        createPluginJar(pluginJar);

        String previousPluginDirectory = System.getProperty("wordl.plugin.dir");
        System.setProperty("wordl.plugin.dir", tempDir.toString());
        try {
            Map<Integer, List<String>> pluginWords = new WordPluginLoader().loadPluginWords();

            assertThat(pluginWords).containsEntry(7, List.of("BALANCE", "CAPTURE"));
        } finally {
            if (previousPluginDirectory == null) {
                System.clearProperty("wordl.plugin.dir");
            } else {
                System.setProperty("wordl.plugin.dir", previousPluginDirectory);
            }
        }
    }

    private void createPluginJar(Path pluginJar) throws IOException {
        Path sourceDirectory = tempDir.resolve("src/de/demo/wordl/sample");
        Path classesDirectory = tempDir.resolve("classes");
        Files.createDirectories(sourceDirectory);
        Files.createDirectories(classesDirectory);

        Path sourceFile = sourceDirectory.resolve("SevenLetterWords.java");
        Files.writeString(sourceFile, """
                package de.demo.wordl.sample;

                import de.demo.wordl.backend.plugin.WordListPlugin;
                import java.util.List;

                public class SevenLetterWords implements WordListPlugin {
                    @Override
                    public int type() {
                        return 7;
                    }

                    @Override
                    public List<String> words() {
                        return List.of("BALANCE", "CAPTURE");
                    }
                }
                """, StandardCharsets.UTF_8);

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        assertThat(compiler).as("Tests require a JDK, not only a JRE.").isNotNull();
        int compilationResult = compiler.run(
                null,
                null,
                null,
                "-classpath",
                System.getProperty("java.class.path"),
                "-d",
                classesDirectory.toString(),
                sourceFile.toString());
        assertThat(compilationResult).isZero();

        try (JarOutputStream jarOutputStream = new JarOutputStream(Files.newOutputStream(pluginJar))) {
            addJarEntry(jarOutputStream, classesDirectory,
                    "de/demo/wordl/sample/SevenLetterWords.class");
            addServiceEntry(jarOutputStream);
        }
    }

    private void addJarEntry(JarOutputStream jarOutputStream, Path classesDirectory, String entryName) throws IOException {
        jarOutputStream.putNextEntry(new JarEntry(entryName));
        Files.copy(classesDirectory.resolve(entryName), jarOutputStream);
        jarOutputStream.closeEntry();
    }

    private void addServiceEntry(JarOutputStream jarOutputStream) throws IOException {
        jarOutputStream.putNextEntry(new JarEntry("META-INF/services/de.demo.wordl.backend.plugin.WordListPlugin"));
        jarOutputStream.write("de.demo.wordl.sample.SevenLetterWords\n".getBytes(StandardCharsets.UTF_8));
        jarOutputStream.closeEntry();
    }
}
