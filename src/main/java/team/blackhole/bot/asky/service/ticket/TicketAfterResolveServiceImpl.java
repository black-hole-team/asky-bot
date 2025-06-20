package team.blackhole.bot.asky.service.ticket;

import com.google.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import team.blackhole.bot.asky.channel.ChannelPool;
import team.blackhole.bot.asky.channel.capability.HubCapability;
import team.blackhole.bot.asky.config.AskyHubConfiguration;
import team.blackhole.bot.asky.db.hibernate.domains.HubTopic;
import team.blackhole.bot.asky.db.hibernate.domains.Ticket;
import team.blackhole.bot.asky.queue.impl.TicketDelayedQueue;
import team.blackhole.bot.asky.service.hub_topic.HubTopicService;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Реализация сервиса {@link TicketAfterResolveService}
 */
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class TicketAfterResolveServiceImpl implements TicketAfterResolveService {

    /** Очередь отложенных обращений на удаление топика */
    private final TicketDelayedQueue ticketDelayedQueue;

    /** Конфигурация хаба */
    private final AskyHubConfiguration hubConfiguration;

    /** Сервис для работы с темами хабов */
    private final HubTopicService hubTopicService;

    /** Пул каналов */
    private final ChannelPool channelPool;

    @Override
    @Transactional
    public Ticket deleteTicketTopics(Ticket ticket) {
        if (hubConfiguration.getDeleteTopicTimeout().isZero()) {
            // Если промежуток между решением обращения и удалением темы хаба равен 0, удаляем её прям сейчас
            deleteHubTopics(Collections.singletonList(ticket.getId()));
        } else {
            // Иначе устанавливаем дату и время запланированного удаления темы хаба
            var topicsDeleteAfter = ZonedDateTime.now(ZoneOffset.UTC).plus(hubConfiguration.getDeleteTopicTimeout());
            for (var topic : ticket.getTopics()) {
                hubTopicService.setDeleteAfter(topic.getId(), topicsDeleteAfter);
            }
            ticketDelayedQueue.add(ticket.getId(), topicsDeleteAfter);
        }
        return ticket;
    }

    @Override
    @Transactional
    public HubTopic continuationTopic(long topicId) {
        return hubTopicService.setDeleteAfter(topicId, ZonedDateTime.now(ZoneOffset.UTC).plus(hubConfiguration.getDeleteTopicTimeout()));
    }

    @Override
    @Transactional
    public void doDeleteTopics() {
        var ticketIds = ticketDelayedQueue.pollExpired();
        if (ticketIds.isEmpty()) {
            return;
        }
        deleteHubTopics(ticketIds);
    }

    /**
     * Удаляет топики обращений которые были закрыты ранее
     * @param ticketIds идентификаторы обращений
     */
    private void deleteHubTopics(List<Long> ticketIds) {
        var now = ZonedDateTime.now(ZoneOffset.UTC);
        var topics = hubTopicService.findHubTopicsByTicketIds(ticketIds);
        var topicsToContinues = new ArrayList<HubTopic>();
        // Выводим сообщение в лог
        log.info("Очистка тем для завершенных обращений: {}", ticketIds);
        // Удаляем темы
        for (var topic : topics) {
            if (now.isBefore(topic.getDeleteTopicAfter())) {
                topicsToContinues.add(topic);
            } else {
                // Получаем хаб темы
                var hub = topic.getHub();
                // Удаляем тему из хаба
                channelPool
                        .getChannelById(hub.getChannelId())
                        .getCapability(HubCapability.class)
                        .ifPresent(hubCapability -> hubCapability.deleteHubTopic(hub.getChannelHubId(), topic.getHubTopicId()));
                // Удаляем тему из БД
                hubTopicService.deleteTopic(topic.getId());
            }
        }
        if (topicsToContinues.isEmpty()) {
            return;
        }
        // Если какие-то темы небыли удалены, то планируем их удаление ещё раз
        for (var topic : topicsToContinues) {
            ticketDelayedQueue.add(topic.getTicket().getId(), topic.getDeleteTopicAfter());
        }
    }
}
