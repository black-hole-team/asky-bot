package team.blackhole.bot.asky.service.hub;

import team.blackhole.bot.asky.db.hibernate.domains.Hub;
import team.blackhole.bot.asky.db.hibernate.domains.HubId;
import team.blackhole.bot.asky.service.hub.data.CreateHubData;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с хабом
 */
public interface HubService {

    /**
     * Создает хаб
     * @param data данные для создания хаба
     */
    Hub create(CreateHubData data);

    /**
     * Возвращает хаб по идентификатору
     * @param hubId идентификатор хаба
     * @return найденный хаб
     */
    Optional<Hub> findById(HubId hubId);

    /**
     * Возвращает список хабов по идентификаторам каналов
     * @param channelIds идентификаторы каналов
     * @return список хабов
     */
    List<Hub> findHubsByChannelId(Collection<String> channelIds);
}
