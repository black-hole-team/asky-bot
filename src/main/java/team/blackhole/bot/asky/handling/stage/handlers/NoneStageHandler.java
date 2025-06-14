package team.blackhole.bot.asky.handling.stage.handlers;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.text.StringSubstitutor;
import org.jetbrains.annotations.NotNull;
import team.blackhole.bot.asky.channel.Channel;
import team.blackhole.bot.asky.channel.ChannelMessage;
import team.blackhole.bot.asky.channel.ChannelPool;
import team.blackhole.bot.asky.channel.capability.ChatCapability;
import team.blackhole.bot.asky.channel.capability.HubCapability;
import team.blackhole.bot.asky.channel.support.ChannelHelper;
import team.blackhole.bot.asky.config.AskyHandlingConfiguration;
import team.blackhole.bot.asky.config.AskyHubConfiguration;
import team.blackhole.bot.asky.db.hibernate.domains.*;
import team.blackhole.bot.asky.db.jedis.domain.Stage;
import team.blackhole.bot.asky.handling.events.HubCreatedEvent;
import team.blackhole.bot.asky.handling.events.MessageEvent;
import team.blackhole.bot.asky.handling.stage.StageCommand;
import team.blackhole.bot.asky.handling.stage.StageCommandHandler;
import team.blackhole.bot.asky.handling.stage.StageCommandScope;
import team.blackhole.bot.asky.security.AskyUserRole;
import team.blackhole.bot.asky.service.chat.ChatService;
import team.blackhole.bot.asky.service.hub.HubService;
import team.blackhole.bot.asky.service.hub.data.CreateHubData;
import team.blackhole.bot.asky.service.ticket.TicketService;
import team.blackhole.bot.asky.service.ticket.data.CreateTicketData;
import team.blackhole.bot.asky.support.MessageSource;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.flywaydb.core.internal.util.ClassUtils.isPresent;

/**
 * Обработчик стадии {@link team.blackhole.bot.asky.handling.stage.StageName#NONE}
 */
@Log4j2
@RequiredArgsConstructor(onConstructor_ = @__(@Inject))
public class NoneStageHandler extends StageCommandHandler {

    /** Шаблон для форматирования даты и времени обращения */
    private static final DateTimeFormatter TICKET_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    /** Пул каналов */
    private final ChannelPool pool;

    /** Конфигурация обработки сообщений */
    private final AskyHandlingConfiguration handlingConfiguration;

    /** Конфигурация хабов */
    private final AskyHubConfiguration askyHubConfiguration;

    /** Источник сообщений */
    private final MessageSource messageSource;

    /** Сервис для работы с хабами */
    private final HubService hubService;

    /** Сервис для работы с обращениями */
    private final TicketService ticketService;

    /** Сервис для работы с чатами */
    private final ChatService chatService;

    /**
     * Обрабатывает все сообщения на текущей стадии
     * @param stage стадия
     * @param event событие получения сообщения
     * @return новая стадия
     */
    @Transactional
    @StageCommand(role = {AskyUserRole.ADMIN, AskyUserRole.OPERATOR, AskyUserRole.COMMON})
    public Stage onMessage(Stage stage, MessageEvent event) {
        var message = event.getMessage();

        // Групповые сообщения из главной темы группы пропускаем
        if (message.topicId() == null && message.chatId() < 0) {
            return Stage.propagation(stage, false);
        }

        // Обрабатываем сообщение в зависимости от наличия topicId
        if (message.topicId() == null) {
            processUserMessage(message);
        } else {
            processOperatorMessage(message);
        }

        return Stage.propagation(stage, false);
    }

    /**
     * Действие при отправке команды /resolve
     * @param stage стадия
     * @param event событие получения сообщения
     * @return новая стадия
     */
    @Transactional
    @StageCommand(value = "/resolve", scopes = {StageCommandScope.TOPIC}, role = {AskyUserRole.ADMIN, AskyUserRole.OPERATOR})
    public Stage onResolve(Stage stage, MessageEvent event) {
        var message = event.getMessage();
        var hubId = new HubId(message.chatId(), message.channelId());

        hubService.findById(hubId)
                .flatMap(hub -> findTopicByIdInHub(hub, message.topicId()))
                .ifPresent(topic -> resolveTicket(message, topic));

        return Stage.propagation(stage, false);
    }

    /**
     * Действие при отправке команды /start
     * @param stage стадия
     * @param event событие получения сообщения
     * @return новая стадия
     */
    @Transactional
    @StageCommand(value = "/start", scopes = {StageCommandScope.COMMON})
    public Stage onStart(Stage stage, MessageEvent event) {
        var message = event.getMessage();
        var chatId = new ChatId(message.chatId(), message.channelId());

        // Создаем чат, если он не был создан ранее
        chatService.findByIdOrCreate(chatId);
        // Отправляем приветственное сообщение
        sendWelcomeMessage(message);

        return Stage.propagation(stage, false);
    }

    /**
     * Действие при отправке команды /register_hub
     * @param stage стадия
     * @param event событие получения сообщения
     * @return новая стадия
     */
    @Transactional
    @StageCommand(value = "/register_hub", role = {AskyUserRole.ADMIN}, scopes = {StageCommandScope.HUB})
    public Stage onRegisterHub(Stage stage, MessageEvent event) {
        var message = event.getMessage();
        var hubId = new HubId(message.chatId(), message.channelId());

        if (hubService.findById(hubId).isPresent()) {
            sendMessageToUser(message, "message$hub_already_exists");
        } else {
            registerNewHub(message);
        }

        return Stage.propagation(stage, false);
    }

    /**
     * Обрабатывает сообщение от пользователя (без topicId)
     * @param message сообщение
     */
    private void processUserMessage(ChannelMessage message) {
        var routeChannelHubs = hubService.findHubsByChannelId(handlingConfiguration.getRouteChannels(message.channelId()));

        // Если не найдены хабы, в каналах заданных правилами маршрутизации
        if (routeChannelHubs.isEmpty()) {
            sendMessageToUser(message, "message$no_hub_found_for_this_channel");
            return;
        }

        // Находим последние открытое обращение или создаем новую
        var ticket = getOrCreateTicket(message);

        // Проверяем что обращение ещё не было решено
        if (ticket.getStatus() == TicketStatus.RESOLVED) {
            sendMessageToUser(message, "message$ticket_already_closed");
            return;
        }

        // Пересылаем сообщение во все хабы
        forwardMessageToHubs(message, ticket, routeChannelHubs);
    }

    /**
     * Обрабатывает сообщение от оператора (с topicId)
     * @param message сообщение
     */
    private void processOperatorMessage(ChannelMessage message) {
        hubService.findById(new HubId(message.chatId(), message.channelId()))
            .flatMap(hub -> hub.getTopics().stream().filter(e -> e.getId() == message.topicId()).findFirst())
            .ifPresent(topic -> {
                var ticket = topic.getTicket();

                // Проверяем что обращение ещё не было решено
                if (ticket.getStatus() == TicketStatus.RESOLVED) {
                    sendMessageToUser(message, "message$ticket_already_closed");
                    return;
                }

                var chatId = ticket.getChat().getId();

                // Обновляем статус обращения
                ticketService.updateTicketStatus(topic.getTicketId(), TicketStatus.ANSWERED);

                // Пересылаем сообщение пользователю
                forwardMessageToUser(message, chatId);
            });
    }

    /**
     * Находит существующую или создает новое обращение
     * @param message сообщение
     * @return обращение
     */
    private Ticket getOrCreateTicket(ChannelMessage message) {
        return ticketService.findLastNonClosedTicketByChatId(message.chatId())
                .orElseGet(() -> ticketService.create(CreateTicketData.builder()
                        .chatId(new ChatId(message.chatId(), message.channelId()))
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
            var hubId = hub.getId();
            var hubChannel = pool.getChannelById(hubId.getChannelId());
            var hubCapability = hubChannel.getCapability(HubCapability.class);
            var chatCapability = hubChannel.getCapability(ChatCapability.class);

            if (hubCapability.isEmpty() || chatCapability.isEmpty()) {
                continue;
            }

            var topicForThisChannel = findOrCreateHubTopic(message, ticket, hub, hubCapability.get(), chatCapability.get());

            // Пересылаем сообщение в хаб
            sendMessageToHub(message, hubId.getId(), topicForThisChannel.getId(), chatCapability.get());

            // Обновляем статус обращения если необходимо
            if (ticket.getStatus() != TicketStatus.OPEN) {
                ticketService.updateTicketStatus(ticket.getId(), TicketStatus.AWAITING_RESPONSE);
            }
        }
    }

    /**
     * Находит существующий или создает новую тему для хаба
     * @param message        сообщение
     * @param ticket         обращение
     * @param hub            хаб
     * @param hubCapability  возможности хаба
     * @param chatCapability возможности чата
     * @return тема
     */
    private HubTopic findOrCreateHubTopic(ChannelMessage message, Ticket ticket, Hub hub, HubCapability hubCapability, ChatCapability chatCapability) {
        var hubId = hub.getId();
        return ticket.getTopics().stream()
                .filter(e -> hubId.getChannelId().equals(e.getHubChannelId()) && hubId.getId() == e.getHubId())
                .findFirst()
                .orElseGet(() -> createNewHubTopic(message, ticket, hub, hubCapability, chatCapability));
    }

    /**
     * Создает новую тему для хаба
     * @param message        сообщение
     * @param ticket         обращение
     * @param hub            хаб
     * @param hubCapability  возможности хаба
     * @param chatCapability возможности чата
     * @return созданная тема
     */
    private HubTopic createNewHubTopic(ChannelMessage message, Ticket ticket, Hub hub, HubCapability hubCapability, ChatCapability chatCapability) {
        var hubId = hub.getId();
        var hubTopic = new HubTopic();

        hubTopic.setHub(hub);
        hubTopic.setTicket(ticket);
        hubTopic.setId(hubCapability.createHubTopic(hubId.getId(), ticket.getSubject()).id());

        ticket.getTopics().add(hubTopic);

        // Отправляем приветственное сообщение в созданную тему
        sendMessageToHub(message, hubId.getId(), hubTopic.getId(), chatCapability,
                getHelloMessage(message, ticket, chatCapability.getChatUserInfo(message.chatId(), message.userId())));

        return hubTopic;
    }

    /**
     * Отправляет сообщение в хаб
     * @param message   исходное сообщение
     * @param hubChatId идентификатор чата хаба
     * @param topicId   идентификатор темы
     * @param chatCapability возможности чата
     */
    private void sendMessageToHub(ChannelMessage message, long hubChatId, long topicId, ChatCapability chatCapability) {
        sendMessageToHub(message, hubChatId, topicId, chatCapability, message.content());
    }

    /**
     * Отправляет сообщение в хаб с указанным текстом
     * @param message        исходное сообщение
     * @param hubChatId      идентификатор чата хаба
     * @param topicId        идентификатор темы
     * @param chatCapability возможности чата
     * @param content        текст сообщения
     */
    private void sendMessageToHub(ChannelMessage message, long hubChatId, long topicId, ChatCapability chatCapability, String content) {
        chatCapability.send(ChatCapability.MessageSending.builder()
                .chatId(hubChatId)
                .attachments(message.attachments())
                .topicId(topicId)
                .content(content)
                .build());
    }

    /**
     * Пересылает сообщение пользователю
     * @param message исходное сообщение
     * @param chatId идентификатор чата пользователя
     */
    private void forwardMessageToUser(ChannelMessage message, ChatId chatId) {
        pool.getChannelById(chatId.getChannelId()).getCapability(ChatCapability.class).ifPresent(chatCapability ->
            chatCapability.send(ChatCapability.MessageSending.builder()
                .chatId(chatId.getId())
                .attachments(message.attachments())
                .content(message.content())
                .build()));
    }

    /**
     * Отправляет сообщение пользователю с указанным ключом локализации
     * @param message исходное сообщение
     * @param messageKey ключ сообщения в ресурсах
     */
    private void sendMessageToUser(ChannelMessage message, String messageKey) {
        ChannelHelper.send(pool.getChannelById(message.channelId()),
                message, messageSource.getMessage(messageKey, message.locale()));
    }

    /**
     * Находит тему по идентификатору в хабе
     * @param hub     хаб
     * @param topicId идентификатор темы
     * @return найденная тема
     */
    private Optional<HubTopic> findTopicByIdInHub(Hub hub, Long topicId) {
        return hub.getTopics().stream().filter(e -> e.getId() == topicId).findFirst();
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
            sendMessageToUser(message, "message$ticket_already_closed");
            return;
        }

        // Переводим обращение в статус "Решен"
        ticketService.updateTicketStatus(topic.getTicketId(), TicketStatus.RESOLVED);

        // Отправляем сообщение пользователю
        sendLocalizedMessageToUser(message, ticket.getChat().getId(), "message$ticket_has_been_resolved_user", ticket.getId());

        // Отправляем сообщение оператору
        sendLocalizedMessageToOperator(message, "message$ticket_has_been_resolved_operator", ticket.getId());
    }

    /**
     * Отправляет локализованное сообщение пользователю
     * @param message    исходное сообщение
     * @param chatId     идентификатор чата пользователя
     * @param messageKey ключ сообщения
     * @param args       аргументы для форматирования сообщения
     */
    private void sendLocalizedMessageToUser(ChannelMessage message, ChatId chatId, String messageKey, Object... args) {
        var chatChannel = pool.getChannelById(chatId.getChannelId());
        chatChannel.getCapability(ChatCapability.class).ifPresent(chatCapability ->
            chatCapability.send(ChatCapability.MessageSending.builder()
                .chatId(chatId.getId())
                .attachments(message.attachments())
                .content(messageSource.getMessage(messageKey, message.locale(), args))
                .build()));
    }

    /**
     * Отправляет локализованное сообщение оператору
     * @param message    исходное сообщение
     * @param messageKey ключ сообщения
     * @param args       аргументы для форматирования сообщения
     */
    private void sendLocalizedMessageToOperator(ChannelMessage message, String messageKey, Object... args) {
        var channel = pool.getChannelById(message.channelId());
        ChannelHelper.send(channel, message, messageSource.getMessage(messageKey, message.locale(), args));
    }

    /**
     * Регистрирует новый хаб
     * @param message исходное сообщение
     */
    private void registerNewHub(ChannelMessage message) {
        var channel = pool.getChannelById(message.channelId());
        // Создаём хаб
        var hub = hubService.create(CreateHubData.builder()
                .channelId(message.channelId())
                .id(message.chatId())
                .name(getHubName(channel, message))
                .build());
        // Отправляем сообщение об успешном создании хаба
        sendLocalizedMessageToOperator(message, "message$hub_created", hub.getName());
    }

    /**
     * Отправляет приветственное сообщение пользователю
     * @param message исходное сообщение
     */
    private void sendWelcomeMessage(ChannelMessage message) {
        var channel = pool.getChannelById(message.channelId());
        channel.getCapability(ChatCapability.class).ifPresent(chat -> {
            var userInfo = chat.getChatUserInfo(message.chatId(), message.userId());
            var welcomeMessage = messageSource.getMessage("message$hello_message", message.locale(), userInfo.firstName());

            chat.send(ChatCapability.MessageSending.builder()
                    .chatId(message.chatId())
                    .content(welcomeMessage)
                    .build());
        });
    }

    /**
     * Возвращает приветственное сообщение
     * @param message исходное сообщение
     * @param ticket  обращение
     * @param user    пользователь чата
     * @return приветственное сообщение
     */
    private String getHelloMessage(ChannelMessage message, Ticket ticket, ChatCapability.ChatUserInfo user) {
        return messageSource.getMessage("message$new_ticket_topic", message.locale(),
                ticket.getSubject(),
                ticket.getId(),
                TICKET_DATE_TIME_FORMATTER.format(ticket.getCreatedAt()),
                Stream.of(user.username() == null ? null : "@%s".formatted(user.username()), user.lastName(), user.firstName())
                        .filter(Objects::nonNull)
                        .map(String::valueOf)
                        .collect(Collectors.joining(", ")));
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

    /**
     * Возвращает наименование хаба
     * @param channel канал
     * @param message исходное сообщение
     * @return имя хаба
     */
    @NotNull
    private static String getHubName(Channel channel, ChannelMessage message) {
        return channel.getCapability(HubCapability.class)
                .map(e -> e.getInfo(message.chatId()))
                .map(HubCapability.HubInfo::name)
                .orElse("#%s".formatted(message.topicId()));
    }
}
