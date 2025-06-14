package team.blackhole.bot.asky.support;

import lombok.Getter;

import java.util.Locale;

/**
 * Реализация локализируемого значения {@link Localizable}
 */
@Getter
public class LocalizableImpl implements Localizable {

    /** Источник сообщений */
    private final MessageSource source;
    /** Ключ сообщения */
    private final String key;
    /** Параметры форматирования */
    private final Object[] args;

    /**
     * Конструктор
     * @param source источник сообщений
     * @param key    ключ сообщения
     * @param args   параметры форматирования
     */
    LocalizableImpl(MessageSource source, String key, Object[] args) {
        this.source = source;
        this.key = key;
        this.args = args;
    }

    @Override
    public String toLocaleString(Locale locale) {
        return source.getMessage(key, locale, args);
    }

    @Override
    public String toString() {
        return toLocaleString(Locale.getDefault());
    }
}
