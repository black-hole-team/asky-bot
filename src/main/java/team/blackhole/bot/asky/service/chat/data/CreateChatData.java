package team.blackhole.bot.asky.service.chat.data;

import lombok.Builder;

/**
 * Данные для создания чата
 * @param channelId     идентификатор канала
 * @param channelChatId идентификатор чата в канале
 */
@Builder
public record CreateChatData(String channelChatId, String channelId) {
}
