package team.blackhole.bot.asky.service.hub_topic;

import team.blackhole.bot.asky.db.hibernate.domains.HubTopic;
import team.blackhole.bot.asky.service.HibernateService;
import team.blackhole.bot.asky.service.hub_topic.data.HubTopicCreateData;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с темами хаба
 */
public interface HubTopicService extends HibernateService<HubTopic, Long> {

    /**
     * Создает тему хаба
     * @param data данные для создания темы хаба
     * @return созданная тема хаба
     */
    HubTopic create(HubTopicCreateData data);

    /**
     * Устанавливает дату и время удаления темы
     * @param id    идентификатор темы хаба
     * @param after дата и время удаления темы
     */
    HubTopic setDeleteAfter(long id, ZonedDateTime after);

    /**
     * Возвращает опциональное значение темы хаба по идентификатору канала, идентификатору хаба в канале и идентификатора темы в хабе
     * @param channelId         идентификатор канала
     * @param channelHubId      идентификатор хаба в канале
     * @param channelHubTopicId идентификатор темы в хабе канала
     * @return опциональное значение топика
     */
    Optional<HubTopic> findHubTopicByChannelIdAndHubIdAndHubTopicId(String channelId, String channelHubId, String channelHubTopicId);

    /**
     * Возвращает опциональное значение темы хаба по идентификатору заявки и идентификатору хаба
     * @param ticketId идентификатор заявки
     * @param hubId    идентификатор хаба
     * @return опциональное значение темы
     */
    Optional<HubTopic> findHubTopicByTicketIdAndHubId(long ticketId, long hubId);

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
}
