package team.blackhole.bot.asky.handling.hooks;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import team.blackhole.bot.asky.handling.events.AbstractEvent;
import team.blackhole.bot.asky.handling.events.HubCreatedEvent;
import team.blackhole.bot.asky.handling.events.MessageEvent;
import team.blackhole.bot.asky.handling.events.TicketCreatedEvent;

/**
 * Тип хука
 */
@Getter
@RequiredArgsConstructor
public enum HookType {

    /** Хук получения нового сообщения */
    MESSAGE(MessageEvent.class),

    /** Хук создания нового хаба */
    HUB_CREATE(HubCreatedEvent.class),

    /** Хук создания нового обращения */
    TICKET_CREATE(TicketCreatedEvent.class);

    /** Тип события */
    private final Class<? extends AbstractEvent> eventType;
}
