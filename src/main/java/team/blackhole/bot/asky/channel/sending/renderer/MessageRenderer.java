package team.blackhole.bot.asky.channel.sending.renderer;

import team.blackhole.bot.asky.channel.capability.ChatCapability;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Отрисовщик содержимого сообщения
 */
public interface MessageRenderer {

    /**
     * Возвращает содержимое сообщения
     * @return содержимое сообщения
     */
    String render(Locale locale);

    /**
     * Возвращает список действий над сообщением
     * @return список действий над сообщением
     */
    default List<List<ChatCapability.MessageAction>> actions(Locale locale) {
        return Collections.emptyList();
    }
}
