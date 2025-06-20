package team.blackhole.bot.asky.service.ticket.data;

import lombok.Builder;

/**
 * Данные для создания обращения
 * @param subject       субъект обращения
 * @param channelId     идентификатор канала
 * @param channelChatId идентификатор чата в канале
 * @param channelUserId идентификатор пользователя, на стороне канала обращения
 */
@Builder
public record CreateTicketData(String subject, String channelId, String channelChatId, long channelUserId) {
}
