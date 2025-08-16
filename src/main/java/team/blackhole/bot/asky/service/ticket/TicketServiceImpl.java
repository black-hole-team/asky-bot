package team.blackhole.bot.asky.service.ticket;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import team.blackhole.bot.asky.db.hibernate.HibernateRepository;
import team.blackhole.bot.asky.db.hibernate.domains.Ticket;
import team.blackhole.bot.asky.db.hibernate.domains.TicketStatus;
import team.blackhole.bot.asky.db.hibernate.repository.TicketRepository;
import team.blackhole.bot.asky.db.support.Page;
import team.blackhole.bot.asky.events.TicketCreatedEvent;
import team.blackhole.bot.asky.events.TicketStatusChangeEvent;
import team.blackhole.bot.asky.service.chat.ChatService;
import team.blackhole.bot.asky.service.ticket.data.CreateTicketData;
import team.blackhole.data.filter.Filter;

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
    public long getNextTicketId() {
        return ticketRepository.getNextTicketId();
    }

    @Override
    @Transactional
    public Ticket create(CreateTicketData data) {
        var ticket = new Ticket();

        ticket.setSubject(data.subject());
        ticket.setTopics(new ArrayList<>());
        ticket.setUserId(data.channelUserId());
        // Получаем или создаем чат
        ticket.setChat(chatService.findChatByChannelChatIdAndChannelId(data.channelId(), data.channelChatId())
                .orElseThrow());
        // Устанавливаем начальный статус OPEN
        ticket.setStatus(TicketStatus.OPEN);
        ticket = ticketRepository.save(ticket);

        eventBus.post(new TicketCreatedEvent(ticket));

        return ticket;
    }

    @Override
    public HibernateRepository<Ticket, Long> getRepository() {
        return ticketRepository;
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

        eventBus.post(new TicketStatusChangeEvent(ticket, ticketPrevStatus));

        return ticket;
    }
}
