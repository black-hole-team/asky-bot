package team.blackhole.bot.asky.support;

import java.util.Locale;

/**
 * Источник сообщений
 */
public interface MessageSource {

    /**
     * Возвращает локализованное сообщение.
     * @param key    ключ сообщения
     * @param locale целевая локаль
     * @param args   аргументы для подстановки
     * @return локализованная строка
     */
    String getMessage(String key, Locale locale, Object... args);

    /**
     * Создает отложенное локализуемое значение.
     * @param key  ключ сообщения
     * @param args аргументы сообщения
     * @return объект для отложенной локализации
     */
    Localizable localize(String key, Object... args);
}
