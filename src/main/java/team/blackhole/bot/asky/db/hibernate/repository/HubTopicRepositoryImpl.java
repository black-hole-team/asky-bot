package team.blackhole.bot.asky.db.hibernate.repository;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import team.blackhole.bot.asky.db.hibernate.AbstractHibernateRepository;
import team.blackhole.bot.asky.db.hibernate.domains.HubTopic;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Реализация репозитория {@link HubTopicRepository}
 */
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class HubTopicRepositoryImpl extends AbstractHibernateRepository<HubTopic, Long> implements HubTopicRepository {

    /** Фабрика сессий */
    private final SessionFactory sessionFactory;

    @Override
    protected SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    @Override
    protected Class<HubTopic> getPersistentClass() {
        return HubTopic.class;
    }

    @Override
    public List<HubTopic> findHubTopicsByTicketId(long ticketId) {
        return sessionFactory.getCurrentSession().createQuery("FROM HubTopic ht WHERE ht.ticket.id = :ticketId", HubTopic.class)
                .setParameter("ticketId", ticketId)
                .getResultList();
    }

    @Override
    public List<HubTopic> findHubTopicsByTicketIds(List<Long> ticketIds) {
        if (ticketIds == null || ticketIds.isEmpty()) {
            return Collections.emptyList();
        }
        return sessionFactory.getCurrentSession().createQuery("FROM HubTopic ht WHERE ht.ticket.id IN :ticketIds", HubTopic.class)
                .setParameter("ticketIds", ticketIds)
                .getResultList();
    }

    @Override
    public Optional<HubTopic> findHubTopicByChannelIdAndHubIdAndHubTopicId(String channelId, String channelHubId, String channelHubTopicId) {
        return Optional.ofNullable(sessionFactory.getCurrentSession().createQuery("FROM HubTopic ht WHERE ht.hub.channelId = :channelId AND ht.hub.channelHubId = :channelHubId AND ht.hubTopicId = :channelHubTopicId", HubTopic.class)
                .setParameter("channelId", channelId)
                .setParameter("channelHubId", channelHubId)
                .setParameter("channelHubTopicId", channelHubTopicId)
                .getSingleResultOrNull());
    }

    @Override
    public Optional<HubTopic> findHubTopicByTicketIdAndHubId(long ticketId, long hubId) {
        return Optional.ofNullable(sessionFactory.getCurrentSession().createQuery("FROM HubTopic ht WHERE ht.hub.id = :hubId AND ht.ticket.id = :ticketId", HubTopic.class)
                .setParameter("ticketId", ticketId)
                .setParameter("hubId", hubId)
                .getSingleResultOrNull());
    }
}
