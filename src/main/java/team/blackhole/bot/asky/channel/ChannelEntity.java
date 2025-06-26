package team.blackhole.bot.asky.channel;

import java.util.Locale;

/**
 * Сущность канала
 */
public interface ChannelEntity {

    /**
     * Возвращает идентификатор чата
     * @return идентификатор чата
     */
    String chatId();

    /**
     * Возвращает идентификатор канала
     * @return идентификатор канала
     */
    String channelId();

    /**
     * Возвращает идентификатор пользователя
     * @return идентификатор пользователя
     */
    long userId();

    /**
     * Возвращает локаль сущности
     * @return локаль сущности
     */
    Locale locale();
}
