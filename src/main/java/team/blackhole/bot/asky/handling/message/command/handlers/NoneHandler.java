package team.blackhole.bot.asky.handling.message.command.handlers;

import com.google.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import team.blackhole.bot.asky.channel.ChannelMessage;
import team.blackhole.bot.asky.channel.ChannelMessageSource;
import team.blackhole.bot.asky.channel.ChannelPool;
import team.blackhole.bot.asky.channel.capability.ChatCapability;
import team.blackhole.bot.asky.channel.sending.MessageSender;
import team.blackhole.bot.asky.config.AskyHandlingConfiguration;
import team.blackhole.bot.asky.db.hibernate.domains.Chat;
import team.blackhole.bot.asky.db.hibernate.domains.TicketStatus;
import team.blackhole.bot.asky.db.jedis.domain.Stage;
import team.blackhole.bot.asky.handling.message.command.CommandContext;
import team.blackhole.bot.asky.handling.message.command.CommandScope;
import team.blackhole.bot.asky.handling.message.command.annotation.AnnotationCommandHandler;
import team.blackhole.bot.asky.handling.message.command.annotation.BeforeCommand;
import team.blackhole.bot.asky.handling.message.command.annotation.Command;
import team.blackhole.bot.asky.security.AskyUser;
import team.blackhole.bot.asky.security.AskyUserRole;
import team.blackhole.bot.asky.service.chat.ChatService;
import team.blackhole.bot.asky.service.chat.data.CreateChatData;
import team.blackhole.bot.asky.service.hub.HubService;
import team.blackhole.bot.asky.service.hub_topic.HubTopicService;
import team.blackhole.bot.asky.service.ticket.TicketAfterResolveService;
import team.blackhole.bot.asky.service.ticket.TicketService;

/**
 * Обработчик стадии {@link team.blackhole.bot.asky.handling.message.StageName#NONE}
 */
@Log4j2
@RequiredArgsConstructor(onConstructor_ = @__(@Inject))
public class NoneHandler extends AnnotationCommandHandler {

    /** Поле данных идентификатора обращения */
    public static final String TICKET_ID_STATE_DATA = "TICKET_ID";

    /** Идентификатор параметра команды идентификатора обращения */
    public static final String TICKET_ID_COMMAND_PARAM = "ticketId";

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

    /** Сервис для работы с чатами */
    private final ChatService chatService;

    /** Вспомогательный класс для обработки методов, не относящихся к слушателям */
    private final NoneHandlerHelper helper;

    /**
     * Действие перед обработкой команды
     * @param context контекст выполнения команды
     * @param message исходное сообщение
     */
    @Transactional
    @BeforeCommand
    public void beforeCommand(CommandContext context, AskyUser user, ChannelMessage message) {
        // Действие, если источником сообщения был чат
        if (message.source() == ChannelMessageSource.CHAT && user.role() == AskyUserRole.COMMON) {
            context.register(Chat.class, () -> chatService.findChatByChannelChatIdAndChannelId(message.channelId(), String.valueOf(message.chatId()))
                    .orElseGet(() -> chatService.create(CreateChatData.builder()
                            .channelChatId(String.valueOf(message.chatId()))
                            .channelId(message.channelId())
                            .build())));
        }
    }

    /**
     * Обрабатывает все сообщения на текущей стадии
     * @param stage стадия
     * @param message исходное сообщение
     * @param user  пользователь
     * @param chat  чат
     */
    @Transactional
    @Command(role = {AskyUserRole.ADMIN, AskyUserRole.OPERATOR, AskyUserRole.COMMON})
    public void onMessage(Stage stage, ChannelMessage message, AskyUser user, Chat chat) {
        // Групповые сообщения из главной темы группы пропускаем
        if (message.topicId() == null && message.source() == ChannelMessageSource.GROUP) {
            return;
        }
        if (user.role() == AskyUserRole.OPERATOR || user.role() == AskyUserRole.ADMIN) {
            helper.processOperatorMessage(message, stage);
        } else {
            helper.processUserMessage(message, chat);
        }
    }

    /**
     * Действие при отправке команды /select_ticket_{:id}
     * @param stage   стадия
     * @param message исходное сообщение
     * @return новая стадия
     */
    @Command(value = "^\\/select_ticket_(?<" + TICKET_ID_COMMAND_PARAM + ">\\d+)$", role = {AskyUserRole.ADMIN, AskyUserRole.OPERATOR}, scopes = {CommandScope.COMMON})
    public Stage onSelectTicket(Stage stage, ChannelMessage message, CommandContext context) {
        var ticketId = Long.parseLong(context.get(TICKET_ID_COMMAND_PARAM));
        var newStage = Stage.propagation(stage, false);
        ticketService.findById(ticketId)
            .ifPresentOrElse(
                ticket -> {
                    if (ticket.getStatus() == TicketStatus.RESOLVED) {
                        messageSender.sendTicketAlreadyClosedMessage(message.channelId(), message.chatId(), message.topicId());
                    } else {
                        hubService.findHubByChannelHubIdAndChannelId(message.channelId(), message.chatId())
                            .ifPresentOrElse(
                                hub -> {
                                    var chat = ticket.getChat();
                                    // Получаем данные пользователя
                                    var user = pool.getChannelById(chat.getChannelId())
                                            .getCapabilityOrThrow(ChatCapability.class)
                                            .getChatUserInfo(chat.getChannelChatId(), ticket.getUserId());
                                    // Отправляем сообщение о создании нового обращения
                                    messageSender.sendNewTicketMessage(message.channelId(), hub.getChannelHubId(), null, hub.getType(),
                                            ticket.getSubject(), ticket.getId(), ticket.getCreatedAt(), user.lastName(), user.firstName(), user.username());
                                    // Обновляем текущую стадию
                                    newStage.setData(NoneHandler.TICKET_ID_STATE_DATA, String.valueOf(ticketId));
                                },
                                () -> messageSender.sendHubNotFoundMessage(message.channelId(), message.chatId())
                            );
                    }
                },
                () -> messageSender.sendTicketNotFoundMessage(message.channelId(), message.chatId(), ticketId)
            );
        // Возвращаем новое состояние
        return newStage;
    }

    /**
     * Действие при отправке команды /resolve
     * @param stage стадия
     * @param message исходное сообщение
     */
    @Transactional
    @Command(value = "^\\/resolve$", scopes = {CommandScope.TOPIC, CommandScope.COMMON}, role = {AskyUserRole.ADMIN, AskyUserRole.OPERATOR})
    public void onResolve(Stage stage, ChannelMessage message) {
        if (message.source() == ChannelMessageSource.CHAT) {
            stage.getData(TICKET_ID_STATE_DATA)
                    .map(Long::parseLong)
                    .flatMap(ticketService::findById)
                    .ifPresent(ticket -> helper.resolveTicket(message, ticket));
        } else {
            // Разрешаем обращение пользователя
            hubTopicService.findHubTopicByChannelIdAndHubIdAndHubTopicId(message.channelId(), message.chatId(), message.topicId())
                    .ifPresent(topic -> helper.resolveTicket(message, topic.getTicket()));
        }
    }

    /**
     * Действие при отправке команды /start
     * @param message исходное сообщение
     */
    @Transactional
    @Command(value = "^\\/start$", scopes = {CommandScope.COMMON})
    public void onStart(ChannelMessage message) {
        // Отправляем приветственное сообщение
        messageSender.sendWelcomeMessage(message.channelId(), message.chatId(), pool.getChannelById(message.channelId())
                .getCapabilityOrThrow(ChatCapability.class)
                .getChatUserInfo(message.chatId(), message.userId())
                .firstName());
    }

    /**
     * Действие при отправке команды /register_hub
     * @param message исходное сообщение
     */
    @Transactional
    @Command(value = "^\\/register_hub$", role = {AskyUserRole.ADMIN, AskyUserRole.OPERATOR}, scopes = {CommandScope.HUB, CommandScope.COMMON})
    public void onRegisterHub(ChannelMessage message) {
        // Если существует хаб по идентификатору канала и идентификатору чата, то выбрасываем ошибку
        if (hubService.findHubByChannelHubIdAndChannelId(message.channelId(), message.chatId()).isPresent()) {
            messageSender.sendHubAlreadyExistsMessage(message.channelId(), message.chatId());
        } else {
            helper.registerNewHub(message);
        }
    }

    /**
     * Действие при отправке команды /continuation
     * @param message исходное сообщение
     */
    @Transactional
    @Command(value = "^\\/continuation$", scopes = {CommandScope.TOPIC})
    public void onContinuation(ChannelMessage message) {
        // Продлеваем время до удаления темы
        hubTopicService.findHubTopicByChannelIdAndHubIdAndHubTopicId(message.channelId(), message.chatId(), message.topicId())
            .map(foundTopic -> ticketAfterResolveService.continuationTopic(foundTopic.getId()))
            .ifPresent(topic -> {
                // Отправляем уведомление о скором удалении темы хаба, если это необходимо
                if (topic.getDeleteTopicAfter() != null) {
                    messageSender.sendThisTopicWillBeDeleted(message.channelId(), message.chatId(), message.topicId(), topic.getDeleteTopicAfter());
                }
            });
    }

    @Override
    protected AskyHandlingConfiguration getHandlingConfiguration() {
        return handlingConfiguration;
    }
}
