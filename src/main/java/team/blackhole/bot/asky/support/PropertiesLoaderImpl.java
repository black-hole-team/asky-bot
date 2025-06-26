package team.blackhole.bot.asky.support;

import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;

/**
 * Базовая реализация загрузчика свойств из файлов ресурсов {@link PropertiesLoader}
 */
@RequiredArgsConstructor
public class PropertiesLoaderImpl implements PropertiesLoader {

    /** Базовый путь для загрузки свойств */
    private final Path basePath;

    @Override
    public Properties loadProperties(String baseName, Locale locale) {
        var resourceName = baseName + "_" + locale.getLanguage() + ".properties";
        try (var stream = Files.newInputStream(basePath.resolve(resourceName))) {
            var props = new Properties();
            props.load(new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)));
            return props;
        } catch (IOException e) {
            throw new MissingResourceException("Ошибка загрузки ресурса: " + e.getMessage(), getClass().getName(), resourceName);
        }
    }
}