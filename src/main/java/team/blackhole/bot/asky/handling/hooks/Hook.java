package team.blackhole.bot.asky.handling.hooks;

import team.blackhole.bot.asky.handling.events.AbstractEvent;

/**
 * Хук для события бота поддержки
 */
public interface Hook extends AutoCloseable {

    /**
     * Обрабатывает событие бота поддержки
     * @param event событие
     */
    void handle(AbstractEvent event);
}
