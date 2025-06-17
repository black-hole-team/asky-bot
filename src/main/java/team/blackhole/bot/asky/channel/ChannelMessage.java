package team.blackhole.bot.asky.channel;

import lombok.Builder;

import java.util.List;
import java.util.Locale;

/**
 * Тип канала, по которому сообщение было получено
 * @param channelType тип канала из которого сообщение пришло
 * @param channelId   идентификатор канала
 * @param id          идентификатор сообщения
 * @param chatId      идентификатор чата
 * @param topicId     идентификатор темы
 * @param userId      идентификатор пользователя
 * @param content     содержимое сообщения
 * @param attachments вложения сообщения
 * @param locale      локаль
 * @param source      источник сообщения
 */
@Builder
public record ChannelMessage(ChannelType channelType, String channelId, long id, String chatId, String topicId, long userId, String content,
                             List<ChannelAttachment> attachments, Locale locale, ChannelMessageSource source) {
}
