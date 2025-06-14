package team.blackhole.bot.asky.support;

import java.util.Locale;
import java.util.Properties;

/**
 * Интерфейс для загрузки свойств локализации
 */
public interface PropertiesLoader {

    /**
     * Загружает свойства для указанной локали
     * @param baseName базовое имя ресурса
     * @param locale локаль для загрузки
     * @return загруженные свойства
     * @throws java.util.MissingResourceException если ресурс не найден или произошла ошибка загрузки
     */
    Properties loadProperties(String baseName, Locale locale);
}