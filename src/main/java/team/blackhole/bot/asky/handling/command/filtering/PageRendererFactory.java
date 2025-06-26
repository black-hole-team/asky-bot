package team.blackhole.bot.asky.handling.command.filtering;

import team.blackhole.bot.asky.channel.sending.renderer.MessageRenderer;

/**
 * Фабрика отрисовщика страниц сущностей
 */
public interface PageRendererFactory {

    /**
     * Возвращает отрисовщик страницы сущностей по фильтру
     * @return отрисовщик страницы
     */
    MessageRenderer create();
}
