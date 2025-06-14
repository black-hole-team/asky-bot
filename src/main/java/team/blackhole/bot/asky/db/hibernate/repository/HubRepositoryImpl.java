package team.blackhole.bot.asky.db.hibernate.repository;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import team.blackhole.bot.asky.db.hibernate.AbstractHibernateRepository;
import team.blackhole.bot.asky.db.hibernate.domains.Hub;
import team.blackhole.bot.asky.db.hibernate.domains.HubId;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Реализация репозитория {@link HubRepository}
 */
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class HubRepositoryImpl extends AbstractHibernateRepository<Hub, HubId> implements HubRepository {

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
        var session = sessionFactory.getCurrentSession();
        return session.createQuery("FROM Hub h WHERE h.id.channelId IN :channelId", Hub.class)
                .setParameter("channelId", channelIds)
                .getResultList();
    }
}
