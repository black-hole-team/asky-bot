package team.blackhole.bot.asky.channel;

import java.util.Locale;

/**
 * Данные обратного вызова при действии пользователя канала
 * @param messageId идентификатор сообщения
 * @param channelId идентификатор канала
 * @param chatId    идентификатор чата
 * @param payload   полезная нагрузка
 * @param userId    идентификатор пользователя
 * @param locale    локаль пользователя
 */
public record ChannelCallback(int messageId, String channelId, String chatId, String payload, long userId, Locale locale) implements ChannelEntity {
}
