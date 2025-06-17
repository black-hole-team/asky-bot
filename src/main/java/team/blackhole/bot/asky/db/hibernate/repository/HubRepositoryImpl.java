package team.blackhole.bot.asky.db.hibernate.repository;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import team.blackhole.bot.asky.db.hibernate.AbstractHibernateRepository;
import team.blackhole.bot.asky.db.hibernate.domains.Hub;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Реализация репозитория {@link HubRepository}
 */
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class HubRepositoryImpl extends AbstractHibernateRepository<Hub, Long> implements HubRepository {

    /** Фабрика сессий */
    private final SessionFactory sessionFactory;

    @Override
    protected SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    @Override
    protected Class<Hub> getPersistentClass() {
        return Hub.class;
    }

    @Override
    public List<Hub> findHubsByChannelId(Collection<String> channelIds) {
        if (channelIds == null || channelIds.isEmpty()) {
            return Collections.emptyList();
        }
        return sessionFactory.getCurrentSession().createQuery("FROM Hub h WHERE h.channelId IN :channelId", Hub.class)
                .setParameter("channelId", channelIds)
                .getResultList();
    }

    @Override
    public Optional<Hub> findHubByChannelHubIdAndChannelId(String channelId, String channelHubId) {
        return Optional.ofNullable(sessionFactory.getCurrentSession().createQuery("FROM Hub h WHERE h.channelId = :channelId AND h.channelHubId = :channelHubId", Hub.class)
                .setParameter("channelId", channelId)
                .setParameter("channelHubId", channelHubId)
                .getSingleResultOrNull());
    }
}
