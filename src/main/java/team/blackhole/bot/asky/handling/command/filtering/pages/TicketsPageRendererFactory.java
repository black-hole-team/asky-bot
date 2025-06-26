package team.blackhole.bot.asky.handling.command.filtering.pages;

import lombok.RequiredArgsConstructor;
import team.blackhole.bot.asky.channel.sending.renderer.MessageRenderer;
import team.blackhole.bot.asky.handling.command.filtering.PageRendererFactory;
import team.blackhole.bot.asky.handling.command.filtering.FilterHelper;
import team.blackhole.bot.asky.service.ticket.TicketService;
import team.blackhole.bot.asky.support.MessageSource;

@RequiredArgsConstructor
public class TicketsPageRendererFactory implements PageRendererFactory {

    /** Сервис для работы с обращениями */
    private final TicketService ticketService;

    /** Источник сообщений */
    private final MessageSource messageSource;

    /** Помощник для работы с фильтром */
    private final FilterHelper stageFilterHelper;

    @Override
    public MessageRenderer create() {
        var filter = stageFilterHelper.getFilter();
        return new TicketsPageRenderer(filter, ticketService.findAll(filter), messageSource);
    }
}
