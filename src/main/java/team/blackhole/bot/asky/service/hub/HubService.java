package team.blackhole.bot.asky.service.hub;

import team.blackhole.bot.asky.db.hibernate.domains.Hub;
import team.blackhole.bot.asky.db.hibernate.domains.HubTopic;
import team.blackhole.bot.asky.service.HibernateService;
import team.blackhole.bot.asky.service.hub.data.CreateHubData;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с хабом
 */
public interface HubService extends HibernateService<Hub, Long> {

    /**
     * Создает хаб
     * @param data данные для создания хаба
     */
    Hub create(CreateHubData data);

    /**
     * Возвращает хаб по идентификатору канала и идентификатору хаба в канале
     * @param channelId     идентификатор канала
     * @param channelHubId идентификатор хаба в канале
     * @return опциональное значение хаба
     */
    Optional<Hub> findHubByChannelHubIdAndChannelId(String channelId, String channelHubId);

    /**
     * Возвращает список хабов по идентификаторам каналов
     * @param channelIds идентификаторы каналов
     * @return список хабов
     */
    List<Hub> findHubsByChannelId(Collection<String> channelIds);
}
