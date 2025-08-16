package team.blackhole.bot.asky.db.hibernate.repository;

import team.blackhole.bot.asky.db.hibernate.HibernateRepository;
import team.blackhole.bot.asky.db.hibernate.domains.Ticket;

import java.util.Optional;

/**
 * Репозиторий для работы с обращениями
 */
public interface TicketRepository extends HibernateRepository<Ticket, Long> {

    /**
     * Возвращает последнее не закрытое обращение по идентификатору чата
     * @param chatId идентификатор чата
     * @return обращение
     */
    Optional<Ticket> findLastNonClosedTicketByChatId(long chatId);

    /**
     * Возвращает следующий идентификатор обращения
     * @return следующий идентификатор обращения
     */
    long getNextTicketId();
}
