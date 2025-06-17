package team.blackhole.bot.asky.db.hibernate.repository;

import team.blackhole.bot.asky.db.hibernate.HibernateRepository;
import team.blackhole.bot.asky.db.hibernate.domains.Hub;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с хабами
 */
public interface HubRepository extends HibernateRepository<Hub, Long> {

    /**
     * Возвращает список хабов по идентификаторам каналов
     * @param channelIds идентификаторы каналов
     * @return список хабов
     */
    List<Hub> findHubsByChannelId(Collection<String> channelIds);

    /**
     * Возвращает хаб по идентификатору канала и идентификатору хаба в канале
     * @param channelId     идентификатор канала
     * @param channelHubId идентификатор хаба в канале
     * @return опциональное значение хаба
     */
    Optional<Hub> findHubByChannelHubIdAndChannelId(String channelId, String channelHubId);
}
