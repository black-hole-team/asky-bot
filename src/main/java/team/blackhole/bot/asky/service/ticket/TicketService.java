package team.blackhole.bot.asky.service.ticket;

import team.blackhole.bot.asky.db.hibernate.domains.Ticket;
import team.blackhole.bot.asky.db.hibernate.domains.TicketStatus;
import team.blackhole.bot.asky.service.ticket.data.CreateTicketData;

import java.util.Optional;

/**
 * Сервис для работы с обращениями
 */
public interface TicketService {

    /**
     * Создает обращение
     * @param data данные для создания обращения
     */
    Ticket create(CreateTicketData data);

    /**
     * Возвращает обращение по идентификатору
     * @param ticketId идентификатор обращения
     * @return найденное обращение
     */
    Optional<Ticket> findById(long ticketId);

    /**
     * Возвращает последнее не закрытое обращение по идентификатору чата
     * @param chatId идентификатор чата
     * @return обращение
     */
    Optional<Ticket> findLastNonClosedTicketByChatId(long chatId);

    /**
     * Обновляет статус обращения
     * @param ticketId идентификатор обращения
     * @param status   статус обращения
     */
    Ticket updateTicketStatus(long ticketId, TicketStatus status);
}
