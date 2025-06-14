package team.blackhole.bot.asky.handling.hooks;

import com.google.common.eventbus.Subscribe;
import lombok.RequiredArgsConstructor;
import team.blackhole.bot.asky.handling.events.AbstractEvent;
import team.blackhole.bot.asky.handling.events.HubCreatedEvent;
import team.blackhole.bot.asky.handling.events.MessageEvent;
import team.blackhole.bot.asky.handling.events.TicketCreatedEvent;

import java.util.Map;

/**
 * Движок выполнения хуков
 */
@RequiredArgsConstructor
public class HookEngine implements AutoCloseable {

    /** Карта, где ключ это тип события хука, а значение это массив хуков */
    private final Map<Class<? extends AbstractEvent>, Hook[]> hooks;

    /**
     * Запускает обработку события при помощи хуков
     * @param event событие для обработки
     */
    public void produce(AbstractEvent event) {
        for (var current : hooks.getOrDefault(event.getClass(), new Hook[0])) {
            current.handle(event);
        }
    }

    /**
     * Действие при получении сообщения
     * @param event событие получения сообщения
     */
    @Subscribe
    public void onMessage(MessageEvent event) {
        produce(event);
    }

    /**
     * Действие при создании нового хаба
     * @param event событие создания нового хаба
     */
    @Subscribe
    public void onHubCreated(HubCreatedEvent event) {
        produce(event);
    }

    /**
     * Действие при создании обращения
     * @param event событие создания обращения
     */
    @Subscribe
    public void onTicketCreate(TicketCreatedEvent event) {
        produce(event);
    }

    @Override
    public void close() throws Exception {
        for (var hooks : hooks.values()) {
            for (var hook : hooks) {
                hook.close();
            }
        }
    }
}
