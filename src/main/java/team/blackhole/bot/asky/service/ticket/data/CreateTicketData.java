package team.blackhole.bot.asky.service.ticket.data;

import lombok.Builder;
import team.blackhole.bot.asky.db.hibernate.domains.ChatId;

/**
 * Данные для создания обращения
 */
@Builder
public record CreateTicketData(String subject, ChatId chatId) {
}
