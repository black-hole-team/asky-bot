package team.blackhole.bot.asky.support;

import java.util.Locale;

/**
 * Локализируемое значение
 */
public interface Localizable {

    /**
     * Возвращает строку для указанной локали.
     * @param locale целевая локаль
     * @return локализованная строка
     */
    String toLocaleString(Locale locale);
}
