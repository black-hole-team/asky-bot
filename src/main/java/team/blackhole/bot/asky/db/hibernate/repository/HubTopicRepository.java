package team.blackhole.bot.asky.db.hibernate.repository;

import team.blackhole.bot.asky.db.hibernate.HibernateRepository;
import team.blackhole.bot.asky.db.hibernate.domains.HubTopic;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с хабами
 */
public interface HubTopicRepository extends HibernateRepository<HubTopic, Long> {

    /**
     * Возвращает список тем хабов, по идентификатору обращения
     * @param ticketId идентификатор обращения
     * @return список тем хаба
     */
    List<HubTopic> findHubTopicsByTicketId(long ticketId);

    /**
     * Возвращает список тем хабов, по идентификаторам обращения
     * @param ticketIds идентификаторы обращений
     * @return список тем хаба
     */
    List<HubTopic> findHubTopicsByTicketIds(List<Long> ticketIds);

    /**
     * Возвращает опциональное значение темы хаба по идентификатору канала, идентификатору хаба в канале и идентификатора темы в хабе
     * @param channelId         идентификатор канала
     * @param channelHubId      идентификатор хаба в канале
     * @param channelHubTopicId идентификатор темы в хабе канала
     * @return опциональное значение темы
     */
    Optional<HubTopic> findHubTopicByChannelIdAndHubIdAndHubTopicId(String channelId, String channelHubId, String channelHubTopicId);

    /**
     * Возвращает опциональное значение темы хаба по идентификатору заявки и идентификатору хаба
     * @param ticketId идентификатор заявки
     * @param hubId    идентификатор хаба
     * @return опциональное значение темы
     */
    Optional<HubTopic> findHubTopicByTicketIdAndHubId(long ticketId, long hubId);
}
