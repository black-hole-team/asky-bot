package team.blackhole.bot.asky.handling.events;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import team.blackhole.bot.asky.db.hibernate.domains.Hub;

/**
 * Событие создания нового хаба
 */
@Getter
@Builder
@RequiredArgsConstructor
public class HubCreatedEvent extends AbstractEvent {

    /** Созданный хаб */
    private final Hub hub;
}
