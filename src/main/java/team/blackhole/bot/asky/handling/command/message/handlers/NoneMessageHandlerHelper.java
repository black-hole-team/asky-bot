package team.blackhole.bot.asky.handling.command.message.handlers;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.text.StringSubstitutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.blackhole.bot.asky.channel.ChannelMessage;
import team.blackhole.bot.asky.channel.ChannelMessageSource;
import team.blackhole.bot.asky.channel.ChannelPool;
import team.blackhole.bot.asky.channel.capability.ChatCapability;
import team.blackhole.bot.asky.channel.capability.HubCapability;
import team.blackhole.bot.asky.channel.sending.MessageSender;
import team.blackhole.bot.asky.config.AskyHandlingConfiguration;
import team.blackhole.bot.asky.config.AskyHubConfiguration;
import team.blackhole.bot.asky.db.hibernate.domains.*;
import team.blackhole.bot.asky.db.jedis.domain.Stage;
import team.blackhole.bot.asky.db.jedis.repository.StageRepository;
import team.blackhole.bot.asky.executable.ExecutableFactory;
import team.blackhole.bot.asky.service.hub.HubService;
import team.blackhole.bot.asky.service.hub.data.CreateHubData;
import team.blackhole.bot.asky.service.hub_topic.HubTopicService;
import team.blackhole.bot.asky.service.hub_topic.data.HubTopicCreateData;
import team.blackhole.bot.asky.service.ticket.TicketAfterResolveService;
import team.blackhole.bot.asky.service.ticket.TicketService;
import team.blackhole.bot.asky.service.ticket.data.CreateTicketData;
import team.blackhole.bot.asky.support.exception.AskyException;

import java.nio.file.Path;
import java.util.HashMap;

/**
 * Вспомогательный класс для {@link NoneMessageHandler}
 */
@RequiredArgsConstructor(onConstructor_ = @__(@Inject))
public class NoneMessageHandlerHelper {

    /** Пул каналов */
    private final ChannelPool pool;

    /** Конфигурация обработки сообщений */
    private final AskyHandlingConfiguration handlingConfiguration;

    /** Сервис для отправки сообщений */
    private final MessageSender messageSender;

    /** Сервис для работы с хабами */
    private final HubService hubService;

    /** Сервис для работы с темами хаба */
    private final HubTopicService hubTopicService;

    /** Сервис для работы с обращениями */
    private final TicketService ticketService;

    /** Сервис для работы с обращениями после их решения */
    private final TicketAfterResolveService ticketAfterResolveService;

    /** Репозитория для работы со стадией */
    private final StageRepository stageRepository;

    /** Фабрика исполняемых пользовательских сценариев */
    private final ExecutableFactory executableFactory;

    /**
     * Обрабатывает сообщение от пользователя (без topicId)
     * @param message сообщение
     * @param chat    чат
     */
    public void processUserMessage(ChannelMessage message, team.blackhole.bot.asky.db.hibernate.domains.Chat chat) {
        var routeChannelHubs = hubService.findHubsByChannelId(handlingConfiguration.getRouteChannels(message.channelId()));
        // Если не найдены хабы, в каналах заданных правилами маршрутизации
        if (routeChannelHubs.isEmpty()) {
            messageSender.sendNoHubFoundForChannelMessage(message.channelId(), message.chatId());
        } else {
            // Находим последние открытое обращение или создаем новую
            var ticket = getOrCreateTicket(message, chat.getId());
            // Проверяем что обращение ещё не было решено
            if (ticket.getValue().getStatus() == TicketStatus.RESOLVED) {
                messageSender.sendTicketAlreadyClosedMessage(message.channelId(), message.chatId(), message.topicId());
            } else {
                // Пересылаем сообщение во все хабы
                forwardMessageToHubs(message, ticket.getValue(), ticket.getKey(), routeChannelHubs);
                // Обновляем статус обращения если необходимо
                if (ticket.getValue().getStatus() != TicketStatus.OPEN) {
                    ticketService.updateTicketStatus(ticket.getValue().getId(), TicketStatus.AWAITING_RESPONSE);
                }
            }
            // Если это новое обращение пробуем обновить стадии обращений в соответствующих хабах
            if (ticket.getKey()) {
                for (var hub : routeChannelHubs) {
                    if (hub.getType() != HubType.SINGLE_CHAT) {
                        continue;
                    }
                    // Если обращение в хабах с типом одиночный чат не выбрано, устанавливаем его как текущее обращение
                    stageRepository.findByChannelTypeAndUserIdAndChatId(hub.getChannelId(), hub.getChannelHubId())
                            .ifPresent(stage -> {
                                if (stage.getData(NoneMessageHandler.TICKET_ID_STATE_DATA).isEmpty()) {
                                    stage.setData(NoneMessageHandler.TICKET_ID_STATE_DATA, String.valueOf(ticket.getValue().getId()));
                                    stageRepository.updateByChannelTypeAndUserIdAndChatId(hub.getChannelId(), hub.getChannelHubId(), stage);
                                }
                            });
                }
            }
        }
    }

    /**
     * Находит существующую или создает новое обращение
     * @param message сообщение
     * @param chatId  идентификатор чата
     * @return пара, где ключ это признак нового обращения, а значение это само обращение
     */
    public Pair<Boolean, Ticket> getOrCreateTicket(ChannelMessage message, long chatId) {
        return ticketService.findLastNonClosedTicketByChatId(chatId)
                .map(ticket -> Pair.of(false, ticket))
                .orElseGet(() -> Pair.of(true, ticketService.create(CreateTicketData.builder()
                        .channelChatId(message.chatId())
                        .channelId(message.channelId())
                        .channelUserId(message.userId())
                        .subject(getTicketSubject(message, ticketService.getNextTicketId()))
                        .build())));
    }

    /**
     * Обрабатывает сообщение от оператора (с topicId)
     * @param message сообщение
     * @param stage   стадия обработки сообщения
     */
    public void processOperatorMessage(ChannelMessage message, Stage stage) {
        if (message.source() == ChannelMessageSource.CHAT) {
            var ticketId = getTicketId(message, stage);
            replyToUser(message, ticketService.findByIdOrThrow(ticketId));
        } else {
            hubTopicService.findHubTopicByChannelIdAndHubIdAndHubTopicId(message.channelId(), message.chatId(), message.topicId())
                    .ifPresent(topic -> replyToUser(message, topic.getTicket()));
        }
    }

    /**
     * Отсылает ответ пользователю
     * @param message входящее сообщение
     * @param ticket  обращение
     */
    public void replyToUser(ChannelMessage message, Ticket ticket) {
        // Проверяем что обращение ещё не было решено
        if (ticket.getStatus() == TicketStatus.RESOLVED) {
            messageSender.sendTicketAlreadyClosedMessage(message.channelId(), message.chatId(), message.topicId());
        } else {
            var chat = ticket.getChat();
            // Обновляем статус обращения
            ticketService.updateTicketStatus(ticket.getId(), TicketStatus.ANSWERED);
            // Пересылаем сообщение пользователю
            messageSender.forwardMessageToUser(message, chat.getChannelId(), chat.getChannelChatId());
        }
    }

    /**
     * Пересылает сообщение во все хабы
     * @param message сообщение
     * @param ticket  обращение
     * @param created признак только-что созданного обращения
     * @param hubs    список хабов
     */
    public void forwardMessageToHubs(ChannelMessage message, Ticket ticket, Boolean created, Iterable<Hub> hubs) {
        var user = pool.getChannelById(message.channelId())
                .getCapabilityOrThrow(ChatCapability.class)
                .getChatUserInfo(message.chatId(), message.userId());
        for (var hub : hubs) {
            var hubTopicId = hub.getType() == HubType.GROUP ?
                    findOrCreateHubTopic(ticket, hub).getHubTopicId() :
                    null;
            // Отправляем сообщение о создании нового обращения
            if (created) {
                messageSender.sendNewTicketMessage(message.channelId(), hub.getChannelHubId(), hubTopicId, hub.getType(), ticket.getSubject(), ticket.getId(),
                        ticket.getCreatedAt(), user.lastName(), user.firstName(), user.username());
            }
            // Пересылаем сообщение в хаб
            messageSender.forwardMessageToHubTopic(hub.getChannelId(), hub.getChannelHubId(), hubTopicId, message);
        }
    }

    /**
     * Находит существующую или создает новую тему для хаба
     * @param ticket  обращение
     * @param hub     хаб
     * @return тема
     */
    public HubTopic findOrCreateHubTopic(Ticket ticket, Hub hub) {
        return hubTopicService.findHubTopicByTicketIdAndHubId(ticket.getId(), hub.getId())
                .orElseGet(() -> createNewHubTopic(ticket, hub));
    }

    /**
     * Создает новую тему для хаба
     * @param ticket  обращение
     * @param hub     хаб
     * @return созданная тема
     */
    public HubTopic createNewHubTopic(Ticket ticket, Hub hub) {
        return hubTopicService.create(HubTopicCreateData.builder()
                .hubId(hub.getId())
                .ticketId(ticket.getId())
                .hubTopicId(pool.getChannelById(hub.getChannelId()).getCapabilityOrThrow(HubCapability.class).createHubTopic(hub.getChannelHubId(), ticket.getSubject()).hubTopicId())
                .build());
    }

    /**
     * Разрешает обращение (переводит в статус "Решен")
     * @param message сообщение
     * @param ticket  обращение
     */
    public void resolveTicket(ChannelMessage message, Ticket ticket) {
        // Проверяем что обращение ещё не было решено
        if (ticket.getStatus() == TicketStatus.RESOLVED) {
            messageSender.sendTicketAlreadyClosedMessage(message.channelId(), message.chatId(), message.topicId());
        } else {
            // Переводим обращение в статус "Решен"
            ticketService.updateTicketStatus(ticket.getId(), TicketStatus.RESOLVED);
            // Уведомляем хабы о решения обращения
            for (var hub : hubService.findHubsByChannelId(handlingConfiguration.getRouteChannels(message.channelId()))) {
                // Планируем удаление тем обращений, если это группа
                if (hub.getType() == HubType.GROUP) {
                    // Планируем удаление тем обращений
                    ticketAfterResolveService.deleteTicketTopics(ticket);
                    // Находим текущую тему
                    var topic = findOrCreateHubTopic(ticket, hub);
                    // Отправляем сообщение оператору
                    messageSender.sendTicketResolvedOperatorMessage(hub.getChannelId(), hub.getChannelHubId(), topic.getHubTopicId(), ticket.getId());
                    // Отправляем уведомление о скором удалении темы хаба, если это запланировано
                    if (topic.getDeleteTopicAfter() != null) {
                        messageSender.sendThisTopicWillBeDeleted(hub.getChannelId(), hub.getChannelHubId(), topic.getHubTopicId(), topic.getDeleteTopicAfter());
                    }
                } else {
                    // Очищаем состояние выбранного обращения
                    stageRepository.findByChannelTypeAndUserIdAndChatId(hub.getChannelId(), hub.getChannelHubId())
                            .ifPresent(stage -> {
                                if (stage.getData(NoneMessageHandler.TICKET_ID_STATE_DATA).isPresent()) {
                                    stage.removeData(NoneMessageHandler.TICKET_ID_STATE_DATA);
                                    stageRepository.updateByChannelTypeAndUserIdAndChatId(hub.getChannelId(), hub.getChannelHubId(), stage);
                                }
                            });
                    // Отправляем сообщение оператору
                    messageSender.sendTicketResolvedOperatorMessage(hub.getChannelId(), hub.getChannelHubId(), null, ticket.getId());
                }
            }
            // Отправляем сообщение пользователю
            messageSender.sendTicketResolvedUserMessage(ticket.getChat().getChannelId(), ticket.getChat().getChannelChatId(), ticket.getId());
        }
    }

    /**
     * Регистрирует новый хаб
     * @param message исходное сообщение
     */
    public void registerNewHub(ChannelMessage message) {
        // Отправляем сообщение об успешном создании хаба
        messageSender.sendHubCreatedMessage(message.channelId(), message.chatId(), message.topicId(), hubService.create(CreateHubData.builder()
                .channelId(message.channelId())
                .channelHubId(message.chatId())
                .type(message.source() == ChannelMessageSource.CHAT ? HubType.SINGLE_CHAT : HubType.GROUP)
                .name(getHubName(message))
                .build()).getName());
    }

    /**
     * Возвращает наименование хаба по данным сообщения
     * @param message данные сообщения
     * @return наименование хаба
     */
    @NotNull
    public String getHubName(ChannelMessage message) {
        if (message.source() == ChannelMessageSource.CHAT) {
            // Получаем данные пользователя
            var user = pool.getChannelById(message.channelId())
                    .getCapabilityOrThrow(ChatCapability.class)
                    .getChatUserInfo(message.chatId(), message.userId());
            return user.username() == null ? "#%s".formatted(message.chatId()) : user.username();
        } else {
            return pool.getChannelById(message.channelId()).getCapability(HubCapability.class)
                    .map(e -> e.getInfo(message.chatId()))
                    .map(HubCapability.HubInfo::name)
                    .orElse("#%s".formatted(message.chatId()));
        }
    }

    /**
     * Возвращает субъект обращения
     * @param message  сообщение
     * @param ticketId следующий идентификатор обращения
     * @return субъект обращения
     */
    public String getTicketSubject(ChannelMessage message, long ticketId) {
        return String.valueOf(executableFactory.create(Path.of("subject"), "getSubject")
                .accept(message, ticketId));
    }

    /**
     * Возвращает идентификатор обращения
     * @param message исходное сообщение
     * @param stage   стадия обработки сообщения
     * @return идентификатор обращения
     */
    public long getTicketId(ChannelMessage message, Stage stage) {
        var ticketId = stage.getData(NoneMessageHandler.TICKET_ID_STATE_DATA)
                .map(Long::parseLong)
                .orElse(null);
        if (ticketId == null) {
            messageSender.sendTicketNotSelectedMessage(message.channelId(), message.chatId());
        } else {
            return ticketId;
        }
        throw new AskyException("Обращение не выбрано");
    }
}
