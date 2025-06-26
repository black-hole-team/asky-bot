package team.blackhole.bot.asky.channel.sending;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import team.blackhole.bot.asky.channel.ChannelAttachment;
import team.blackhole.bot.asky.channel.ChannelMessage;
import team.blackhole.bot.asky.channel.ChannelPool;
import team.blackhole.bot.asky.channel.capability.ChatCapability;
import team.blackhole.bot.asky.channel.sending.renderer.MessageRenderer;
import team.blackhole.bot.asky.config.AskyHubConfiguration;
import team.blackhole.bot.asky.db.hibernate.domains.HubType;
import team.blackhole.bot.asky.security.AskyUserRole;
import team.blackhole.bot.asky.support.MessageSource;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Реализация сервиса для отправки сообщений
 */
@RequiredArgsConstructor(onConstructor_ = @__(@Inject))
public class MessageSenderImpl implements MessageSender {

    /** Локаль отправляемого сообщения */
    public static final ThreadLocal<Locale> SENDING_LOCALE = ThreadLocal.withInitial(Locale::getDefault);

    /** Шаблон для форматирования даты и времени обращения */
    private static final DateTimeFormatter TICKET_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    /** Пул каналов */
    private final ChannelPool pool;

    /** Источник сообщений */
    private final MessageSource messageSource;

    /** Конфигурация хабов */
    private final AskyHubConfiguration hubConfiguration;

    @Override
    public void send(String channelId, String channelChatId, String topicId, MessageRenderer renderer) {
        pool.getChannelById(channelId).getCapabilityOrThrow(ChatCapability.class).send(ChatCapability.MessageSending.builder()
                .chatId(channelChatId)
                .topicId(topicId)
                .content(renderer.render(SENDING_LOCALE.get()))
                .actions(renderer.actions(SENDING_LOCALE.get()))
                .build());
    }

    @Override
    public void edit(String channelId, String channelChatId, int messageId, MessageRenderer renderer) {
        pool.getChannelById(channelId).getCapabilityOrThrow(ChatCapability.class).edit(ChatCapability.MessageEdit.builder()
                .messageId(messageId)
                .chatId(channelChatId)
                .content(renderer.render(SENDING_LOCALE.get()))
                .actions(renderer.actions(SENDING_LOCALE.get()))
                .build());
    }

    @Override
    public void sendNoHubFoundForChannelMessage(String channelId, String chatId) {
        sendSimpleMessage(channelId, chatId, null, "message$no_hub_found_for_this_channel");
    }

    @Override
    public void sendTicketAlreadyClosedMessage(String channelId, String channelChatId, String topicId) {
        sendSimpleMessage(channelId, channelChatId, topicId, "message$ticket_already_closed");
    }

    @Override
    public void sendHubAlreadyExistsMessage(String channelId, String chatId) {
        sendSimpleMessage(channelId, chatId, null, "message$hub_already_exists");
    }

    @Override
    public void sendTicketNotFoundMessage(String channelId, String channelChatId, long ticketId) {
        sendSimpleMessage(channelId, channelChatId, null, "message$ticket_not_found", ticketId);
    }

    @Override
    public void sendHubNotFoundMessage(String channelId, String channelChatId) {
        sendSimpleMessage(channelId, channelChatId, null, "message$hub_not_found");
    }

    @Override
    public void sendTicketResolvedUserMessage(String channelId, String channelChatId, long ticketId) {
        sendSimpleMessage(channelId, channelChatId, null, "message$ticket_has_been_resolved_user", ticketId);
    }

    @Override
    public void sendTicketResolvedOperatorMessage(String channelId, String channelChatId, String topicId, long ticketId) {
        sendSimpleMessage(channelId, channelChatId, topicId, "message$ticket_has_been_resolved_operator", ticketId);
    }

    @Override
    public void sendHubCreatedMessage(String channelId, String channelChatId, String topicId, String hubName) {
        sendSimpleMessage(channelId, channelChatId, topicId, "message$hub_created", hubName);
    }

    @Override
    public void sendWelcomeMessage(String channelId, String channelChatId, String userFirstName) {
        sendSimpleMessage(channelId, channelChatId, null, "message$hello_message", userFirstName);
    }

    @Override
    public void sendHelpMessage(String channelId, String channelChatId, String topicId, AskyUserRole role) {
        sendSimpleMessage(channelId, channelChatId, topicId, "message$%s_help".formatted(role.name().toLowerCase()));
    }

    @Override
    public void sendTicketNotSelectedMessage(String channelId, String channelChatId) {
        sendSimpleMessage(channelId, channelChatId, null, "message$ticket_not_selected");
    }

    @Override
    public void forwardMessageToHubTopic(String channelId, String channelHubId, String channelHubIdTopicId, ChannelMessage message) {
        sendMessageToHubTopic(channelId, channelHubId, channelHubIdTopicId, message.content(), message.attachments());
    }

    @Override
    public void forwardMessageToUser(ChannelMessage message, String channelId, String channelChatId) {
        pool.getChannelById(channelId).getCapability(ChatCapability.class).ifPresent(chatCapability ->
            chatCapability.send(ChatCapability.MessageSending.builder()
                .chatId(channelChatId)
                .attachments(message.attachments())
                .content(message.content())
                .build()));
    }

    @Override
    public void sendNewTicketMessage(String channelId, String channelHubId, String channelHubIdTopicId, HubType hubType, String ticketSubject, long ticketId,
                                     LocalDateTime ticketCreatedAt, String userLastName, String userFirstName, String userUsername) {
        sendSimpleMessage(channelId, channelHubId, channelHubIdTopicId, "message$new_ticket_" + hubType.name().toLowerCase(),
                ticketSubject,
                ticketId,
                TICKET_DATE_TIME_FORMATTER.format(ticketCreatedAt),
                Stream.of(userUsername == null ? null : "@%s".formatted(userUsername), userLastName, userFirstName)
                        .filter(Objects::nonNull).map(String::valueOf).collect(Collectors.joining(", ")));
    }

    @Override
    public void sendThisTopicWillBeDeleted(String channelId, String channelHubId, String channelHubIdTopicId, ZonedDateTime deleteTopicAfter) {
        sendSimpleMessage(channelId, channelHubId, channelHubIdTopicId, "message$this_topic_will_be_deleted",
                formatDuration(Duration.between(ZonedDateTime.now(ZoneOffset.UTC), deleteTopicAfter)),
                TICKET_DATE_TIME_FORMATTER.format(deleteTopicAfter.withZoneSameInstant(hubConfiguration.getTimezone())));
    }

    /**
     * Отправляет сообщение в тему хаба
     * @param channelId           идентификатор канала
     * @param channelHubId        идентификатор хаба канала
     * @param channelHubIdTopicId идентификатор темы в хабе канала
     * @param content             содержимое сообщения
     * @param attachments         вложения
     */
    public void sendMessageToHubTopic(String channelId, String channelHubId, String channelHubIdTopicId, String content, List<ChannelAttachment> attachments) {
        pool.getChannelById(channelId).getCapability(ChatCapability.class).ifPresent(chatCapability -> chatCapability.send(ChatCapability.MessageSending.builder()
                .chatId(channelHubId)
                .attachments(attachments)
                .topicId(channelHubIdTopicId)
                .content(content)
                .build()));
    }

    /**
     * Отправляет простое сообщение с указанным ключом локализации
     * @param channelId  идентификатор канала
     * @param chatId     идентификатор чата
     * @param topicId    идентификатор темы чата
     * @param messageKey ключ сообщения
     * @param format     параметры форматирования
     */
    private void sendSimpleMessage(String channelId, String chatId, String topicId, String messageKey, Object... format) {
        pool.getChannelById(channelId).getCapability(ChatCapability.class).ifPresent(chatCapability -> chatCapability.send(ChatCapability.MessageSending.builder()
            .chatId(chatId)
            .topicId(topicId)
            .content(messageSource.getMessage(messageKey, SENDING_LOCALE.get(), format))
            .build()));
    }

    /**
     * Форматирует продолжительность
     * @param duration продолжительность
     * @return отформатированная продолжительность
     */
    private String formatDuration(Duration duration) {
        return Stream.of(
                Pair.of(new String[]{"день", "дня", "дней"}, duration.toDays()),
                Pair.of(new String[]{"час", "часа", "часов"}, duration.toHours() % 24),
                Pair.of(new String[]{"минута", "минуты", "минут"}, duration.toMinutes() % 60),
                Pair.of(new String[]{"секунда", "секунды", "секунд"}, duration.getSeconds() % 60)
            )
                .filter(unit -> unit.getRight() > 0)
                .map(unit -> {
                    var forms = unit.getLeft();
                    return unit.getRight() + " " + switch (unit.getRight().intValue() % 10) {
                        case 1 -> forms[0];
                        case 2, 3, 4 -> forms[1];
                        default -> forms[2];
                    };
                })
                .collect(Collectors.joining(", "));
    }
}
