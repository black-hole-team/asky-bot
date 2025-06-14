package team.blackhole.bot.asky.service.ticket;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import team.blackhole.bot.asky.db.hibernate.domains.Ticket;
import team.blackhole.bot.asky.db.hibernate.domains.TicketStatus;
import team.blackhole.bot.asky.db.hibernate.repository.TicketRepository;
import team.blackhole.bot.asky.handling.events.HubCreatedEvent;
import team.blackhole.bot.asky.handling.events.TicketCreatedEvent;
import team.blackhole.bot.asky.handling.events.TicketStatusChangeEventCreatedEvent;
import team.blackhole.bot.asky.service.chat.ChatService;
import team.blackhole.bot.asky.service.ticket.data.CreateTicketData;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Реализация сервиса для работы с обращениями {@link TicketService}
 */
@RequiredArgsConstructor(onConstructor_ = @__(@Inject))
public class TicketServiceImpl implements TicketService {

    /** Репозиторий для работы с обращениями */
    private final TicketRepository ticketRepository;

    /** Сервис для работы с чатами */
    private final ChatService chatService;

    /** Шина событий */
    private final EventBus eventBus;

    @Override
    @Transactional
    public Ticket create(CreateTicketData data) {
        var ticket = new Ticket();

        ticket.setSubject(data.subject());
        ticket.setTopics(new ArrayList<>());
        // Получаем или создаем чат
        ticket.setChat(chatService.findByIdOrCreate(data.chatId()));
        // Устанавливаем начальный статус OPEN
        ticket.setStatus(TicketStatus.OPEN);
        ticket = ticketRepository.save(ticket);

        eventBus.post(new TicketCreatedEvent(ticket));

        return ticket;
    }

    @Override
    public Optional<Ticket> findById(long ticketId) {
        return ticketRepository.findById(ticketId);
    }

    @Override
    public Optional<Ticket> findLastNonClosedTicketByChatId(long chatId) {
        return ticketRepository.findLastNonClosedTicketByChatId(chatId);
    }

    @Override
    @Transactional
    public Ticket updateTicketStatus(long ticketId, TicketStatus status) {
        var ticket = findById(ticketId).orElseThrow();
        var ticketPrevStatus = ticket.getStatus();

        ticket.setStatus(status);
        ticket = ticketRepository.save(ticket);

        eventBus.post(new TicketStatusChangeEventCreatedEvent(ticket, ticketPrevStatus));

        return ticket;
    }
}
