package team.blackhole.bot.asky.channel.sending;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import team.blackhole.bot.asky.channel.ChannelAttachment;
import team.blackhole.bot.asky.channel.ChannelMessage;
import team.blackhole.bot.asky.channel.ChannelPool;
import team.blackhole.bot.asky.channel.capability.ChatCapability;
import team.blackhole.bot.asky.config.AskyHubConfiguration;
import team.blackhole.bot.asky.db.hibernate.domains.HubTopic;
import team.blackhole.bot.asky.support.MessageSource;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
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
    public void sendTicketResolvedUserMessage(String channelId, String channelChatId, long ticketId) {
        pool.getChannelById(channelId).getCapability(ChatCapability.class).ifPresent(chatCapability ->
            chatCapability.send(ChatCapability.MessageSending.builder()
                .chatId(channelChatId)
                .content(messageSource.getMessage("message$ticket_has_been_resolved_user", SENDING_LOCALE.get(), ticketId))
                .build()));
    }

    @Override
    public void sendTicketResolvedOperatorMessage(String channelId, String channelChatId, String topicId, long ticketId) {
        pool.getChannelById(channelId).getCapability(ChatCapability.class).ifPresent(chatCapability -> {
            chatCapability.send(ChatCapability.MessageSending.builder()
                .chatId(channelChatId)
                .topicId(topicId)
                .content(messageSource.getMessage("message$ticket_has_been_resolved_operator", SENDING_LOCALE.get(), ticketId))
                .build());
        });
    }

    @Override
    public void sendHubCreatedMessage(String channelId, String channelChatId, String topicId, String hubName) {
        pool.getChannelById(channelId).getCapability(ChatCapability.class).ifPresent(chatCapability -> {
            chatCapability.send(ChatCapability.MessageSending.builder()
                .chatId(channelChatId)
                .topicId(topicId)
                .content(messageSource.getMessage("message$hub_created", SENDING_LOCALE.get(), hubName))
                .build());
        });
    }

    @Override
    public void sendWelcomeMessage(String channelId, String channelChatId, String userFirstName) {
        pool.getChannelById(channelId).getCapability(ChatCapability.class).ifPresent(chat -> chat.send(ChatCapability.MessageSending.builder()
                .chatId(channelChatId)
                .content(messageSource.getMessage("message$hello_message", SENDING_LOCALE.get(), userFirstName))
                .build()));
    }

    @Override
    public void forwardMessageToHubTopic(ChannelMessage message, HubTopic topic) {
        var hub = topic.getHub();
        sendMessageToHubTopic(hub.getChannelId(), hub.getChannelHubId(), topic.getHubTopicId(), message.content(), message.attachments());
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
    public void sendNewTicketTopicMessage(String channelId, String channelHubId, String channelHubIdTopicId, String ticketSubject, long ticketId,
                                          LocalDateTime ticketCreatedAt, String userLastName, String userFirstName, String userUsername) {
        sendMessageToHubTopic(channelId, channelHubId, channelHubIdTopicId, messageSource.getMessage("message$new_ticket_topic", SENDING_LOCALE.get(),
                ticketSubject, ticketId, TICKET_DATE_TIME_FORMATTER.format(ticketCreatedAt), Stream.of(userUsername == null ? null : "@%s".formatted(userUsername), userLastName, userFirstName)
                        .filter(Objects::nonNull)
                        .map(String::valueOf)
                        .collect(Collectors.joining(", "))), Collections.emptyList());
    }

    @Override
    public void sendThisTopicWillBeDeleted(String channelId, String channelHubId, String channelHubIdTopicId, ZonedDateTime deleteTopicAfter) {
        sendMessageToHubTopic(channelId, channelHubId, channelHubIdTopicId, messageSource.getMessage("message$this_topic_will_be_deleted", SENDING_LOCALE.get(),
                formatDuration(Duration.between(ZonedDateTime.now(ZoneOffset.UTC), deleteTopicAfter)),
                TICKET_DATE_TIME_FORMATTER.format(deleteTopicAfter.withZoneSameInstant(hubConfiguration.getTimezone()))), Collections.emptyList());
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
     */
    private void sendSimpleMessage(String channelId, String chatId, String topicId, String messageKey) {
        pool.getChannelById(channelId).getCapability(ChatCapability.class).ifPresent(chatCapability -> {
            chatCapability.send(ChatCapability.MessageSending.builder()
                .chatId(chatId)
                .topicId(topicId)
                .content(messageSource.getMessage(messageKey, SENDING_LOCALE.get()))
                .build());
        });
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
