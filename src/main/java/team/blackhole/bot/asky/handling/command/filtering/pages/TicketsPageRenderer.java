package team.blackhole.bot.asky.handling.command.filtering.pages;

import team.blackhole.bot.asky.db.hibernate.domains.Ticket;
import team.blackhole.bot.asky.db.support.Page;
import team.blackhole.bot.asky.handling.command.filtering.PageRenderer;
import team.blackhole.bot.asky.support.MessageSource;
import team.blackhole.data.filter.Filter;

/**
 * Отрисовщик страницы обращений
 */
public class TicketsPageRenderer extends PageRenderer<Ticket> {

    /** Префикс списка */
    public static final String PREFIX = "ticket";

    /**
     * Конструктор
     * @param filter        фильтр
     * @param page          страница
     * @param messageSource источник сообщений
     */
    public TicketsPageRenderer(Filter filter, Page<Ticket> page, MessageSource messageSource) {
        super(filter, page, messageSource);
    }

    @Override
    protected String getPrefix() {
        return PREFIX;
    }

    @Override
    protected Object[] getRowFormat(Ticket entity) {
        return new Object[] {
            entity.getId(),
            entity.getSubject(),
            entity.getStatus()
        };
    }
}
