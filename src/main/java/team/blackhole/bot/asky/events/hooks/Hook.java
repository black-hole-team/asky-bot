package team.blackhole.bot.asky.events.hooks;

import team.blackhole.bot.asky.events.AbstractEvent;

/**
 * Хук для события бота поддержки
 */
public interface Hook {

    /**
     * Обрабатывает событие бота поддержки
     * @param event событие
     */
    void handle(AbstractEvent event);
}
