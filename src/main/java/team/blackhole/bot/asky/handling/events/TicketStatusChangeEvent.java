package team.blackhole.bot.asky.handling.events;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import team.blackhole.bot.asky.db.hibernate.domains.Ticket;
import team.blackhole.bot.asky.db.hibernate.domains.TicketStatus;

/**
 * Событие изменения статуса обращения
 */
@Getter
@Builder
@RequiredArgsConstructor
public class TicketStatusChangeEvent extends AbstractEvent {

    /** Обращение */
    private final Ticket ticket;

    /** Предыдущий статус обращения */
    private final TicketStatus prevStatus;
}
