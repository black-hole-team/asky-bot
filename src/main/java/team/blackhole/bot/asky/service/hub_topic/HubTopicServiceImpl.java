package team.blackhole.bot.asky.service.hub_topic;

import com.google.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import team.blackhole.bot.asky.db.hibernate.HibernateRepository;
import team.blackhole.bot.asky.db.hibernate.domains.HubTopic;
import team.blackhole.bot.asky.db.hibernate.domains.HubType;
import team.blackhole.bot.asky.db.hibernate.repository.HubTopicRepository;
import team.blackhole.bot.asky.service.hub.HubService;
import team.blackhole.bot.asky.service.hub_topic.data.HubTopicCreateData;
import team.blackhole.bot.asky.service.ticket.TicketService;
import team.blackhole.bot.asky.support.exception.AskyException;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Реализация сервиса {@link HubTopicService}
 */
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class HubTopicServiceImpl implements HubTopicService {

    /** Репозиторий для работы с темами хабов */
    private final HubTopicRepository hubTopicRepository;

    /** Сервис для работы с хабами */
    private final HubService hubService;

    /** Сервис для работы с обращениями */
    private final TicketService ticketService;

    @Override
    @Transactional
    public HubTopic create(HubTopicCreateData data) {
        var hubTopic = new HubTopic();
        var hub = hubService.findById(data.hubId()).orElseThrow();

        if (hub.getType() == HubType.SINGLE_CHAT) {
            throw new AskyException("Хаб организованный как чат не может иметь тем");
        }

        hubTopic.setHub(hub);
        hubTopic.setTicket(ticketService.findById(data.ticketId()).orElseThrow());
        hubTopic.setHubTopicId(data.hubTopicId());

        return hubTopicRepository.save(hubTopic);
    }

    @Override
    @Transactional
    public HubTopic setDeleteAfter(long topicId, ZonedDateTime after) {
        var topic = hubTopicRepository.findById(topicId)
                .orElseThrow();
        topic.setDeleteTopicAfter(after);
        return hubTopicRepository.save(topic);
    }

    @Override
    public Optional<HubTopic> findHubTopicByChannelIdAndHubIdAndHubTopicId(String channelId, String channelHubId, String channelHubTopicId) {
        return hubTopicRepository.findHubTopicByChannelIdAndHubIdAndHubTopicId(channelId, channelHubId, channelHubTopicId);
    }

    @Override
    public Optional<HubTopic> findHubTopicByTicketIdAndHubId(long ticketId, long hubId) {
        return hubTopicRepository.findHubTopicByTicketIdAndHubId(ticketId, hubId);
    }

    @Override
    public List<HubTopic> findHubTopicsByTicketId(long ticketId) {
        return hubTopicRepository.findHubTopicsByTicketId(ticketId);
    }

    @Override
    public List<HubTopic> findHubTopicsByTicketIds(List<Long> ticketIds) {
        return hubTopicRepository.findHubTopicsByTicketIds(ticketIds);
    }

    @Override
    public HibernateRepository<HubTopic, Long> getRepository() {
        return hubTopicRepository;
    }
}
