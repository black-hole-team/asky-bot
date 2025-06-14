package team.blackhole.bot.asky.db.hibernate.repository;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import team.blackhole.bot.asky.db.hibernate.AbstractHibernateRepository;
import team.blackhole.bot.asky.db.hibernate.domains.Chat;
import team.blackhole.bot.asky.db.hibernate.domains.ChatId;

/**
 * Реализация репозитория {@link ChatRepository}
 */
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class ChatRepositoryImpl extends AbstractHibernateRepository<Chat, ChatId> implements ChatRepository {

    /** Фабрика сессий */
    private final SessionFactory sessionFactory;

    @Override
    protected SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    @Override
    protected Class<Chat> getPersistentClass() {
        return Chat.class;
    }
}
