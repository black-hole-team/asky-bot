package team.blackhole.bot.asky.handling.command.filtering;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;
import team.blackhole.bot.asky.handling.command.filtering.pages.TicketsPageRendererFactory;
import team.blackhole.bot.asky.service.ticket.TicketService;
import team.blackhole.bot.asky.support.MessageSource;

/**
 * Поставщик фабрик отрисовщика страниц сущностей
 */
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class PageRendererFactoryProvider {

    /** Сервис для работы с обращениями */
    private final TicketService ticketService;

    /** Источник сообщений */
    private final MessageSource messageSource;

    /**
     * Создает новый экземпляр помощника для работы с фильтром
     * @param helper помощник для работы с фильтром стадии
     * @return помощник для работы с фильтром
     */
    public PageRendererFactory create(FilterHelper helper) {
        return new TicketsPageRendererFactory(ticketService, messageSource, helper);
    }
}
