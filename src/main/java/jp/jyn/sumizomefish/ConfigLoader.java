package jp.jyn.sumizomefish;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigLoader {
    private final Logger logger = LogManager.getLogger(ConfigLoader.class);

    public ConfigLoader(Path file) {
        if (Files.notExists(file)) {
            createDefaultConfig(file);
        }
        Config config = ConfigFactory.parseFile(file.toFile());

        config.entrySet().forEach(e -> System.setProperty(e.getKey(), e.getValue().unwrapped().toString()));
        System.getProperties().forEach((k, v) -> logger.debug("{}: {}", k, v));
    }

    public static void load(Path file) {
        new ConfigLoader(file);
    }

    private void createDefaultConfig(Path file) {
        try (InputStream in = ClassLoader.getSystemResourceAsStream("BOOT-INF/classes/sumizomefish.conf")) {
            Files.copy(in, file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
