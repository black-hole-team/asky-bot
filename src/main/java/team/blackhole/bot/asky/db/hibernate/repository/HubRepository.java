package team.blackhole.bot.asky.db.hibernate.repository;

import team.blackhole.bot.asky.db.hibernate.HibernateRepository;
import team.blackhole.bot.asky.db.hibernate.domains.Hub;
import team.blackhole.bot.asky.db.hibernate.domains.HubId;

import java.util.Collection;
import java.util.List;

/**
 * Репозиторий для работы с хабами
 */
public interface HubRepository extends HibernateRepository<Hub, HubId> {

    /**
     * Возвращает список хабов по идентификаторам каналов
     * @param channelIds идентификаторы каналов
     * @return список хабов
     */
    List<Hub> findHubsByChannelId(Collection<String> channelIds);
}
