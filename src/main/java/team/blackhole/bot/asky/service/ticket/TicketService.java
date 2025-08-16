package team.blackhole.bot.asky.service.ticket;

import team.blackhole.bot.asky.db.hibernate.domains.Ticket;
import team.blackhole.bot.asky.db.hibernate.domains.TicketStatus;
import team.blackhole.bot.asky.service.HibernateService;
import team.blackhole.bot.asky.service.ticket.data.CreateTicketData;

import java.util.Optional;

/**
 * Сервис для работы с обращениями
 */
public interface TicketService extends HibernateService<Ticket, Long> {

    /**
     * Возвращает следующий идентификатор обращения
     * @return следующий идентификатор обращения
     */
    long getNextTicketId();

    /**
     * Создает обращение
     * @param data данные для создания обращения
     */
    Ticket create(CreateTicketData data);

    /**
     * Возвращает последнее не закрытое обращение по идентификатору чата
     * @param chatId идентификатор чата
     * @return обращение
     */
    Optional<Ticket> findLastNonClosedTicketByChatId(long chatId);

    /**
     * Обновляет статус обращения
     * @param id     идентификатор обращения
     * @param status статус обращения
     */
    Ticket updateTicketStatus(long id, TicketStatus status);
}
