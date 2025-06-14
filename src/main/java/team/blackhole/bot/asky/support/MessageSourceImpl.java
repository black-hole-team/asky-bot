package team.blackhole.bot.asky.support;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Реализация источника сообщений {@link MessageSource}
 */
@RequiredArgsConstructor
public class MessageSourceImpl implements MessageSource {

    /** Карта, где ключ это локаль, а значение это кеш бандла */
    private final Map<Locale, BundleCache> bundleCacheMap = new ConcurrentHashMap<>();

    /** Локаль, которая используется при невозможности найти бандл с нужной локалью */
    private final Locale fallbackLocale;

    /** Базовое имя бандла */
    private final String baseName;

    /** Время кеширования в миллисекундах */
    private final long cacheTTLMillis;

    /** Загрузчик свойств */
    private final PropertiesLoader propertiesLoader;

    @Override
    public String getMessage(String key, Locale locale, Object... args) {
        try {
            var cache = getBundleCache(locale);
            var format = cache.getMessageFormats().get(key);

            if (format == null) {
                throw new MissingResourceException("Сообщение по ключу не найдено: " + key, getClass().getName(), key);
            }

            return MessageFormat.format(format, args);
        } catch (MissingResourceException e) {
            if (locale == fallbackLocale) {
                throw e;
            }
            return getMessage(key, fallbackLocale, args);
        }
    }

    @Override
    public Localizable localize(String key, Object... args) {
        return new LocalizableImpl(this, key, args);
    }

    /**
     * Возвращает кеш бандла
     * @param locale локаль
     * @return кеш бандла
     */
    private BundleCache getBundleCache(Locale locale) {
        return bundleCacheMap.compute(locale, (loc, currentCache) -> {
            // Проверяем необходимость обновления кэша
            if (currentCache != null && !isCacheExpired(currentCache)) {
                return currentCache;
            }

            // Создаем новый кэш
            var formats = new HashMap<String, String>();
            var props = propertiesLoader.loadProperties(baseName, locale);

            for (var key : props.stringPropertyNames()) {
                formats.put(key, props.getProperty(key));
            }

            return new BundleCache(formats);
        });
    }

    /**
     * Возвращает признак истекшего кеша бандла
     * @param cache кеш бандла
     * @return {@code true}, если кеш истёк {@code false} если иначе
     */
    private boolean isCacheExpired(BundleCache cache) {
        if (cacheTTLMillis < 0) {
            return false;
        }

        return System.currentTimeMillis() - cache.getLoadTime() > cacheTTLMillis;
    }

    /**
     * Внутренний класс для кэширования MessageFormat.
     */
    @Getter
    private static class BundleCache {

        /** Время загрузки кеша */
        private final long loadTime;
        /** Карта, где ключ это ключ локализации и значение это шаблон строки */
        private final Map<String, String> messageFormats;

        /**
         * Конструктор
         * @param messageFormats карта, где ключ это ключ локализации и значение это шаблон строки
         */
        BundleCache(Map<String, String> messageFormats) {
            this.messageFormats = Collections.unmodifiableMap(messageFormats);
            this.loadTime = System.currentTimeMillis();
        }
    }
}
