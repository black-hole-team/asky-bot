package team.blackhole.bot.asky.events;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import team.blackhole.bot.asky.db.hibernate.domains.Ticket;

/**
 * Событие создания нового обращения
 */
@Getter
@Builder
@RequiredArgsConstructor
public class TicketCreatedEvent extends AbstractEvent {

    /** Обращение */
    private final Ticket ticket;
}
