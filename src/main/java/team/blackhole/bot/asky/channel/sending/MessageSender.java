package team.blackhole.bot.asky.channel.sending;

import team.blackhole.bot.asky.channel.ChannelMessage;
import team.blackhole.bot.asky.channel.sending.renderer.MessageRenderer;
import team.blackhole.bot.asky.db.hibernate.domains.HubType;
import team.blackhole.bot.asky.security.AskyUserRole;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * Сервис для отправки сообщений
 */
public interface MessageSender {

    /**
     * Отправляет сообщение
     * @param channelId     идентификатор канала
     * @param channelChatId идентификатор чата в канале
     * @param topicId       идентификатор темы хаба
     * @param renderer      отрисовщик сообщения
     */
    void send(String channelId, String channelChatId, String topicId, MessageRenderer renderer);

    /**
     * Редактирует сообщение
     * @param channelId     идентификатор канала
     * @param channelChatId идентификатор чата в канале
     * @param messageId     идентификатор сообщения в чате
     * @param renderer      отрисовщик сообщения
     */
    void edit(String channelId, String channelChatId, int messageId, MessageRenderer renderer);

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
     * Отправляет сообщение о не найденном обращении
     * @param channelId     идентификатор канала
     * @param channelChatId идентификатор чата в канале
     * @param ticketId      идентификатор обращения
     */
    void sendTicketNotFoundMessage(String channelId, String channelChatId, long ticketId);

    /**
     * Отправляет сообщение о не найденном хабе
     * @param channelId     идентификатор канала
     * @param channelChatId идентификатор чата в канале
     */
    void sendHubNotFoundMessage(String channelId, String channelChatId);

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
     * Отсылает сообщение со справкой по командам бота
     * @param channelId     идентификатор канала
     * @param channelChatId идентификатор чата в канале
     * @param topicId       идентификатор темы
     * @param role          роль пользователя
     */
    void sendHelpMessage(String channelId, String channelChatId, String topicId, AskyUserRole role);

    /**
     * Отправляет сообщение о том, что ни одно обращение не выбрано
     * @param channelId     идентификатор канала
     * @param channelChatId идентификатор чата в канале
     */
    void sendTicketNotSelectedMessage(String channelId, String channelChatId);

    /**
     * Пересылает сообщение в хаб с текстом из исходного сообщения
     * @param channelId           идентификатор канала
     * @param channelHubId        идентификатор хаба канала
     * @param channelHubIdTopicId идентификатор темы в хабе канала
     * @param message             исходное сообщение
     */
    void forwardMessageToHubTopic(String channelId, String channelHubId, String channelHubIdTopicId, ChannelMessage message);

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
     * @param hubType             тип хаба
     * @param ticketSubject       субъект заявки
     * @param ticketId            идентификатор заявки
     * @param ticketCreatedAt     дата и время создания заявки
     * @param userFirstName       имя пользователя
     * @param userLastName        фамилия пользователя
     * @param userUsername        тег пользователя
     */
    void sendNewTicketMessage(String channelId, String channelHubId, String channelHubIdTopicId, HubType hubType, String ticketSubject, long ticketId,
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
