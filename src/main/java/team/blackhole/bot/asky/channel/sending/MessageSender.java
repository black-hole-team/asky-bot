package team.blackhole.bot.asky.channel.sending;

import team.blackhole.bot.asky.channel.ChannelMessage;
import team.blackhole.bot.asky.db.hibernate.domains.HubTopic;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * Сервис для отправки сообщений
 */
public interface MessageSender {

    /**
     * Отправляет сообщение о том, что не найдены хабы для данного канала
     * @param channelId     идентификатор канала
     * @param channelChatId идентификатор чата в канале
     */
    void sendNoHubFoundForChannelMessage(String channelId, String channelChatId);

    /**
     * Отправляет сообщение о том, что хаб уже существует
     * @param channelId     идентификатор канала
     * @param channelChatId идентификатор чата в канале
     */
    void sendHubAlreadyExistsMessage(String channelId, String channelChatId);

    /**
     * Отправляет сообщение о том, что обращение уже закрыто
     * @param channelId     идентификатор канала
     * @param channelChatId идентификатор чата в канале
     * @param topicId       идентификатор темы хаба
     */
    void sendTicketAlreadyClosedMessage(String channelId,  String channelChatId, String topicId);

    /**
     * Отправляет сообщение о том, что обращение было разрешено (пользователю)
     * @param channelId     идентификатор канала
     * @param channelChatId идентификатор чата в канале
     * @param ticketId      идентификатор обращения
     */
    void sendTicketResolvedUserMessage(String channelId, String channelChatId, long ticketId);

    /**
     * Отправляет сообщение о том, что обращение было разрешено (оператору)
     * @param channelId     идентификатор канала
     * @param channelChatId идентификатор чата в канале
     * @param topicId       идентификатор темы
     * @param ticketId      идентификатор обращения
     */
    void sendTicketResolvedOperatorMessage(String channelId, String channelChatId, String topicId, long ticketId);

    /**
     * Отправляет сообщение о том, что хаб был создан
     * @param channelId     идентификатор канала
     * @param channelChatId идентификатор чата в канале
     * @param topicId       идентификатор темы
     * @param hubName       имя хаба
     */
    void sendHubCreatedMessage(String channelId, String channelChatId, String topicId, String hubName);

    /**
     * Отправляет приветственное сообщение пользователю
     * @param channelId     идентификатор канала
     * @param channelChatId идентификатор чата в канале
     * @param userFirstName имя пользователя
     */
    void sendWelcomeMessage(String channelId, String channelChatId, String userFirstName);

    /**
     * Пересылает сообщение в хаб с текстом из исходного сообщения
     * @param message исходное сообщение
     * @param topic   тема для пересылки
     */
    void forwardMessageToHubTopic(ChannelMessage message, HubTopic topic);

    /**
     * Пересылает сообщение пользователю
     * @param message       исходное сообщение
     * @param channelId     канал, в который необходимо переслать сообщение
     * @param channelChatId чат канала, в который необходимо переслать сообщение
     */
    void forwardMessageToUser(ChannelMessage message, String channelId, String channelChatId);

    /**
     * Отправляет приветственное сообщение в хаб с информацией о новом обращении
     * @param channelId           идентификатор канала
     * @param channelHubId        идентификатор хаба канала
     * @param channelHubIdTopicId идентификатор темы в хабе канала
     * @param ticketSubject       субъект заявки
     * @param ticketId            идентификатор заявки
     * @param ticketCreatedAt     дата и время создания заявки
     * @param userFirstName       имя пользователя
     * @param userLastName        фамилия пользователя
     * @param userUsername        тег пользователя
     */
    void sendNewTicketTopicMessage(String channelId, String channelHubId, String channelHubIdTopicId, String ticketSubject, long ticketId,
                                          LocalDateTime ticketCreatedAt, String userLastName, String userFirstName, String userUsername);


    /**
     * Отправляет сообщение о скором удалении темы хаба
     * @param channelId           идентификатор канала
     * @param channelHubId        идентификатор хаба канала
     * @param channelHubIdTopicId идентификатор темы в хабе канала
     * @param deleteTopicAfter    дата и время запланированного удаления темы
     */
    void sendThisTopicWillBeDeleted(String channelId, String channelHubId, String channelHubIdTopicId, ZonedDateTime deleteTopicAfter);
}
