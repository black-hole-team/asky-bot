package team.blackhole.bot.asky.handling.command.handlers;

import com.google.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.text.StringSubstitutor;
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
import team.blackhole.bot.asky.handling.command.CommandScope;
import team.blackhole.bot.asky.handling.command.annotation.AnnotationCommandHandler;
import team.blackhole.bot.asky.handling.command.annotation.BeforeCommand;
import team.blackhole.bot.asky.handling.command.annotation.Command;
import team.blackhole.bot.asky.handling.command.annotation.CommandContext;
import team.blackhole.bot.asky.handling.events.MessageEvent;
import team.blackhole.bot.asky.security.AskyUserRole;
import team.blackhole.bot.asky.service.chat.ChatService;
import team.blackhole.bot.asky.service.chat.data.CreateChatData;
import team.blackhole.bot.asky.service.hub.HubService;
import team.blackhole.bot.asky.service.hub.data.CreateHubData;
import team.blackhole.bot.asky.service.hub_topic.HubTopicService;
import team.blackhole.bot.asky.service.hub_topic.data.HubTopicCreateData;
import team.blackhole.bot.asky.service.ticket.TicketAfterResolveService;
import team.blackhole.bot.asky.service.ticket.TicketService;
import team.blackhole.bot.asky.service.ticket.data.CreateTicketData;

import java.util.HashMap;

/**
 * Обработчик стадии {@link team.blackhole.bot.asky.handling.command.StageName#NONE}
 */
@Log4j2
@RequiredArgsConstructor(onConstructor_ = @__(@Inject))
public class NoneHandler extends AnnotationCommandHandler {

    /** Пул каналов */
    private final ChannelPool pool;

    /** Конфигурация обработки сообщений */
    private final AskyHandlingConfiguration handlingConfiguration;

    /** Конфигурация хабов */
    private final AskyHubConfiguration askyHubConfiguration;

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

    /** Сервис для работы с чатами */
    private final ChatService chatService;

    /**
     * Действие перед обработкой команды
     * @param context контекст выполнения команды
     * @param event   событие получения сообщения
     */
    @Transactional
    @BeforeCommand
    public void beforeCommand(CommandContext context, MessageEvent event) {
        var message = event.getMessage();
        // Действие, если источником сообщения был чат
        if (message.source() == ChannelMessageSource.CHAT) {
            // Создаём чат, если он не был создан ранее
            var chat = chatService.findChatByChannelChatIdAndChannelId(message.channelId(), String.valueOf(message.chatId()))
                    .orElseGet(() -> chatService.create(CreateChatData.builder()
                        .channelChatId(String.valueOf(message.chatId()))
                        .channelId(message.channelId())
                    .build()));
            // Регистрируем его, чтобы он был доступен при вызове команды
            context.register(Chat.class, () -> chat);
        }
    }

    /**
     * Обрабатывает все сообщения на текущей стадии
     * @param stage стадия
     * @param event событие получения сообщения
     * @param chat  чат
     * @return новая стадия
     */
    @Transactional
    @Command(role = {AskyUserRole.ADMIN, AskyUserRole.OPERATOR, AskyUserRole.COMMON})
    public Stage onMessage(Stage stage, MessageEvent event, Chat chat) {
        var message = event.getMessage();
        // Групповые сообщения из главной темы группы пропускаем
        if (message.topicId() == null && message.source() == ChannelMessageSource.GROUP) {
            return Stage.propagation(stage, false);
        }
        // Обрабатываем сообщение в зависимости от наличия topicId
        if (message.topicId() == null) {
            processUserMessage(message, chat);
        } else {
            processOperatorMessage(message);
        }
        // Возвращаем исходное состояние
        return Stage.propagation(stage, false);
    }

    /**
     * Действие при отправке команды /resolve
     * @param stage стадия
     * @param event событие получения сообщения
     * @return новая стадия
     */
    @Transactional
    @Command(value = "/resolve", scopes = {CommandScope.TOPIC}, role = {AskyUserRole.ADMIN, AskyUserRole.OPERATOR})
    public Stage onResolve(Stage stage, MessageEvent event) {
        var message = event.getMessage();
        // Разрешаем обращение пользователя
        hubTopicService.findHubTopicByChannelIdAndHubIdAndHubTopicId(message.channelId(), message.chatId(), message.topicId())
                .ifPresent(topic -> resolveTicket(message, topic));
        // Возвращаем исходное состояние
        return Stage.propagation(stage, false);
    }

    /**
     * Действие при отправке команды /start
     * @param stage стадия
     * @param event событие получения сообщения
     * @return новая стадия
     */
    @Transactional
    @Command(value = "/start", scopes = {CommandScope.COMMON})
    public Stage onStart(Stage stage, MessageEvent event) {
        var message = event.getMessage();
        // Отправляем приветственное сообщение
        messageSender.sendWelcomeMessage(message.channelId(), message.chatId(), pool.getChannelById(message.channelId())
                .getCapabilityOrThrow(ChatCapability.class)
                .getChatUserInfo(message.chatId(), message.userId())
                .firstName());
        // Возвращаем исходное состояние
        return Stage.propagation(stage, false);
    }

    /**
     * Действие при отправке команды /register_hub
     * @param stage стадия
     * @param event событие получения сообщения
     * @return новая стадия
     */
    @Transactional
    @Command(value = "/register_hub", role = {AskyUserRole.ADMIN}, scopes = {CommandScope.HUB})
    public Stage onRegisterHub(Stage stage, MessageEvent event) {
        var message = event.getMessage();
        // Если существует хаб по идентификатору канала и идентификатору чата, то выбрасываем ошибку
        if (hubService.findHubByChannelHubIdAndChannelId(message.channelId(), message.chatId()).isPresent()) {
            messageSender.sendHubAlreadyExistsMessage(message.channelId(), message.chatId());
        } else {
            registerNewHub(message);
        }
        // Возвращаем исходное состояние
        return Stage.propagation(stage, false);
    }

    /**
     * Действие при отправке команды /continuation
     * @param stage стадия
     * @param event событие получения сообщения
     * @return новая стадия
     */
    @Transactional
    @Command(value = "/continuation", scopes = {CommandScope.TOPIC})
    public Stage onContinuation(Stage stage, MessageEvent event) {
        var message = event.getMessage();
        // Продлеваем время до удаления темы
        hubTopicService.findHubTopicByChannelIdAndHubIdAndHubTopicId(message.channelId(), message.chatId(), message.topicId())
            .map(foundTopic -> ticketAfterResolveService.continuationTopic(foundTopic.getId()))
            .ifPresent(topic -> {
                // Отправляем уведомление о скором удалении темы хаба, если это необходимо
                if (topic.getDeleteTopicAfter() != null) {
                    messageSender.sendThisTopicWillBeDeleted(message.channelId(), message.chatId(), message.topicId(), topic.getDeleteTopicAfter());
                }
            });
        // Возвращаем исходное состояние
        return Stage.propagation(stage, false);
    }

    /**
     * Обрабатывает сообщение от пользователя (без topicId)
     * @param message сообщение
     * @param chat    чат
     */
    private void processUserMessage(ChannelMessage message, Chat chat) {
        var routeChannelHubs = hubService.findHubsByChannelId(handlingConfiguration.getRouteChannels(message.channelId()));
        // Если не найдены хабы, в каналах заданных правилами маршрутизации
        if (routeChannelHubs.isEmpty()) {
            messageSender.sendNoHubFoundForChannelMessage(message.channelId(), message.chatId());
        } else {
            // Находим последние открытое обращение или создаем новую
            var ticket = getOrCreateTicket(message, chat.getId());
            // Проверяем что обращение ещё не было решено
            if (ticket.getStatus() == TicketStatus.RESOLVED) {
                messageSender.sendTicketAlreadyClosedMessage(message.channelId(), message.chatId(), message.topicId());
            } else {
                // Пересылаем сообщение во все хабы
                forwardMessageToHubs(message, ticket, routeChannelHubs);
            }
        }
    }

    /**
     * Обрабатывает сообщение от оператора (с topicId)
     * @param message сообщение
     */
    private void processOperatorMessage(ChannelMessage message) {
        hubTopicService.findHubTopicByChannelIdAndHubIdAndHubTopicId(message.channelId(), message.chatId(), message.topicId()).ifPresent(topic -> {
            var ticket = topic.getTicket();
            // Проверяем что обращение ещё не было решено
            if (ticket.getStatus() == TicketStatus.RESOLVED) {
                messageSender.sendTicketAlreadyClosedMessage(message.channelId(), message.chatId(), message.topicId());
            } else {
                var chat = ticket.getChat();
                // Обновляем статус обращения
                ticketService.updateTicketStatus(topic.getTicket().getId(), TicketStatus.ANSWERED);
                // Пересылаем сообщение пользователю
                messageSender.forwardMessageToUser(message, chat.getChannelId(), chat.getChannelChatId());
            }
        });
    }

    /**
     * Находит существующую или создает новое обращение
     * @param message сообщение
     * @param chatId  идентификатор чата
     * @return обращение
     */
    private Ticket getOrCreateTicket(ChannelMessage message, long chatId) {
        return ticketService.findLastNonClosedTicketByChatId(chatId).orElseGet(() -> ticketService.create(CreateTicketData.builder()
                        .channelChatId(message.chatId())
                        .channelId(message.channelId())
                        .subject(getTicketSubject(message))
                        .build()));
    }

    /**
     * Пересылает сообщение во все хабы
     * @param message сообщение
     * @param ticket  обращение
     * @param hubs    список хабов
     */
    private void forwardMessageToHubs(ChannelMessage message, Ticket ticket, Iterable<Hub> hubs) {
        for (var hub : hubs) {
            // Пересылаем сообщение в хаб
            messageSender.forwardMessageToHubTopic(message, findOrCreateHubTopic(message, ticket, hub));

            // Обновляем статус обращения если необходимо
            if (ticket.getStatus() != TicketStatus.OPEN) {
                ticketService.updateTicketStatus(ticket.getId(), TicketStatus.AWAITING_RESPONSE);
            }
        }
    }

    /**
     * Находит существующую или создает новую тему для хаба
     * @param message сообщение
     * @param ticket  обращение
     * @param hub     хаб
     * @return тема
     */
    private HubTopic findOrCreateHubTopic(ChannelMessage message, Ticket ticket, Hub hub) {
        return hubTopicService.findHubTopicByTicketIdAndHubId(ticket.getId(), hub.getId())
                .orElseGet(() -> createNewHubTopic(message, ticket, hub));
    }

    /**
     * Создает новую тему для хаба
     * @param message        сообщение
     * @param ticket         обращение
     * @param hub            хаб
     * @return созданная тема
     */
    private HubTopic createNewHubTopic(ChannelMessage message, Ticket ticket, Hub hub) {
        var hubChannel = pool.getChannelById(hub.getChannelId());
        var hubTopic = hubTopicService.create(HubTopicCreateData.builder()
                .hubId(hub.getId())
                .ticketId(ticket.getId())
                .hubTopicId(hubChannel.getCapabilityOrThrow(HubCapability.class).createHubTopic(hub.getChannelHubId(), ticket.getSubject()).hubTopicId())
                .build());
        var user = hubChannel.getCapabilityOrThrow(ChatCapability.class).getChatUserInfo(message.chatId(), message.userId());

        // Отправляем сообщение о создании темы хаба
        messageSender.sendNewTicketTopicMessage(message.channelId(), hub.getChannelHubId(), hubTopic.getHubTopicId(), ticket.getSubject(), ticket.getId(),
                ticket.getCreatedAt(), user.lastName(), user.firstName(), user.username());

        return hubTopic;
    }


    /**
     * Разрешает обращение (переводит в статус "Решен")
     * @param message сообщение
     * @param topic   тема
     */
    private void resolveTicket(ChannelMessage message, HubTopic topic) {
        var ticket = topic.getTicket();
        // Проверяем что обращение ещё не было решено
        if (ticket.getStatus() == TicketStatus.RESOLVED) {
            messageSender.sendTicketAlreadyClosedMessage(message.channelId(), message.chatId(), message.topicId());
        } else {
            // Переводим обращение в статус "Решен"
            ticketService.updateTicketStatus(ticket.getId(), TicketStatus.RESOLVED);
            // Планируем удаление тем обращений
            ticketAfterResolveService.deleteTicketTopics(ticket);
            // Отправляем сообщение пользователю
            messageSender.sendTicketResolvedUserMessage(message.channelId(), ticket.getChat().getChannelChatId(), ticket.getId());
            // Отправляем сообщение оператору
            messageSender.sendTicketResolvedOperatorMessage(message.channelId(), message.chatId(), message.topicId(), ticket.getId());
            // Отправляем уведомление о скором удалении темы хаба, если это необходимо
            if (topic.getDeleteTopicAfter() != null) {
                messageSender.sendThisTopicWillBeDeleted(message.channelId(), message.chatId(), message.topicId(), topic.getDeleteTopicAfter());
            }
        }
    }

    /**
     * Регистрирует новый хаб
     * @param message исходное сообщение
     */
    private void registerNewHub(ChannelMessage message) {
        // Отправляем сообщение об успешном создании хаба
        messageSender.sendHubCreatedMessage(message.channelId(), message.chatId(), message.topicId(), hubService.create(CreateHubData.builder()
                .channelId(message.channelId())
                .channelHubId(message.chatId())
                .name(pool.getChannelById(message.channelId()).getCapability(HubCapability.class)
                        .map(e -> e.getInfo(message.chatId()))
                        .map(HubCapability.HubInfo::name)
                        .orElse("#%s".formatted(message.topicId())))
                .build()).getName());
    }


    /**
     * Возвращает субъект обращения
     * @param message сообщение
     * @return субъект обращения
     */
    private String getTicketSubject(ChannelMessage message) {
        var values = new HashMap<String, String>();
        values.put("channelId", message.channelId());
        values.put("userId", String.valueOf(message.userId()));
        values.put("chatId", String.valueOf(message.chatId()));
        values.put("locale", String.valueOf(message.locale()));
        values.put("messageId", String.valueOf(message.id()));
        return StringSubstitutor.replace(askyHubConfiguration.getSubjectNamePattern(), values);
    }
}
