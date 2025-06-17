package team.blackhole.bot.asky.db.hibernate.repository;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import team.blackhole.bot.asky.db.hibernate.AbstractHibernateRepository;
import team.blackhole.bot.asky.db.hibernate.domains.Chat;

import java.util.Optional;

/**
 * Реализация репозитория {@link ChatRepository}
 */
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class ChatRepositoryImpl extends AbstractHibernateRepository<Chat, Long> implements ChatRepository {

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

    @Override
    public Optional<Chat> findChatByChannelChatIdAndChannelId(String channelId, String channelChatId) {
        return Optional.ofNullable(sessionFactory.getCurrentSession().createQuery("FROM Chat c WHERE c.channelId = :channelId AND c.channelChatId = :channelChatId", Chat.class)
                .setParameter("channelId", channelId)
                .setParameter("channelChatId", channelChatId)
                .getSingleResultOrNull());
    }
}
