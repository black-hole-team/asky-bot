package team.blackhole.bot.asky.events.hooks;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import team.blackhole.bot.asky.events.*;

/**
 * Тип хука
 */
@Getter
@RequiredArgsConstructor
public enum HookType {

    /** Хук получения нового сообщения */
    MESSAGE(MessageEvent.class),

    /** Хук получения данных обратного вызова */
    CALLBACK(CallbackEvent.class),

    /** Хук создания нового хаба */
    HUB_CREATE(HubCreatedEvent.class),

    /** Хук создания нового обращения */
    TICKET_CREATE(TicketCreatedEvent.class);

    /** Тип события */
    private final Class<? extends AbstractEvent> eventType;
}
