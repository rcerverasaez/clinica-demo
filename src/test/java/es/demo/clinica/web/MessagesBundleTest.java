package es.demo.clinica.web;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;

/** Comprueba que el español y el inglés tienen exactamente las mismas claves. */
class MessagesBundleTest {

    @Test
    void englishHasTheSameKeysAsSpanish() throws IOException {
        assertEquals(new TreeSet<>(load("messages.properties").stringPropertyNames()),
                new TreeSet<>(load("messages_en.properties").stringPropertyNames()));
    }

    private Properties load(String name) throws IOException {
        Properties properties = new Properties();
        try (Reader reader = new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream(name), StandardCharsets.UTF_8)) {
            properties.load(reader);
        }
        return properties;
    }
}
