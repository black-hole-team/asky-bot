package team.blackhole.bot.asky.db.hibernate.repository;

import com.google.inject.Inject;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import team.blackhole.bot.asky.db.hibernate.AbstractHibernateRepository;
import team.blackhole.bot.asky.db.hibernate.domains.Ticket;
import team.blackhole.bot.asky.db.hibernate.domains.TicketStatus;

import java.util.Optional;

/**
 * Реализация репозитория {@link TicketRepository}
 */
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class TicketRepositoryImpl extends AbstractHibernateRepository<Ticket, Long> implements TicketRepository {

    /** Фабрика сессий */
    private final SessionFactory sessionFactory;

    @Override
    protected SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    @Override
    protected Class<Ticket> getPersistentClass() {
        return Ticket.class;
    }

    @Override
    public Optional<Ticket> findLastNonClosedTicketByChatId(long chatId) {
        try {
            var query = sessionFactory.getCurrentSession()
                    .createQuery("SELECT t FROM Ticket t WHERE t.chat.id = :chatId AND t.status <> :status ORDER BY t.id DESC", Ticket.class);

            query.setParameter("chatId", chatId);
            query.setParameter("status", TicketStatus.RESOLVED);

            query.setMaxResults(1);

            return Optional.ofNullable(query.getSingleResultOrNull());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
