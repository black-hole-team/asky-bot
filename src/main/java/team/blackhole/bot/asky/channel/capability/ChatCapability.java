package team.blackhole.bot.asky.channel.capability;

import lombok.Builder;
import team.blackhole.bot.asky.channel.ChannelAttachment;
import team.blackhole.bot.asky.channel.ChannelCapability;

import java.util.List;

/**
 * Возможность канала управления группами
 */
public interface ChatCapability extends ChannelCapability {

    /**
     * Отправляет сообщение в чат
     * @param sending отправление
     */
    void send(MessageSending sending);

    /**
     * Редактирует сообщение
     * @param edit данные для редактирования сообщения
     */
    void edit(MessageEdit edit);

    /**
     * Возвращает информацию о пользователя чата
     * @param chatId идентификатор чата
     * @param userId идентификатор пользователя
     */
    ChatUserInfo getChatUserInfo(String chatId, long userId);

    /**
     * Данные для редактирования сообщения
     * @param chatId    идентификатор
     * @param messageId идентификатор сообщения
     * @param content   содержимое сообщения
     * @param actions   действия над сообщением
     */
    @Builder
    record MessageEdit(String chatId, int messageId, String content, List<List<MessageAction>> actions) {
    }

    /**
     * Данные для отправки сообщения
     * @param chatId      идентификатор чата
     * @param topicId     идентификатор темы
     * @param replyTo     идентификатор сообщения для ответа
     * @param content     содержимое сообщения
     * @param attachments вложения сообщения
     * @param actions     действия над сообщением
     */
    @Builder
    record MessageSending(String chatId, String topicId, Long replyTo, String content, List<ChannelAttachment> attachments,
                          List<List<MessageAction>> actions) {
    }

    /**
     * Информация о пользователе чата
     * @param userId     идентификатор пользователя
     * @param username   имя пользователя (может быть null)
     * @param firstName  имя (может быть null)
     * @param lastName   фамилия (может быть null)
     */
    @Builder
    record ChatUserInfo(long userId, String username, String firstName, String lastName) {

    }

    /**
     * Действие над сообщением
     * @param text    текст действия
     * @param payload полезная нагрузка действия
     */
    @Builder
    record MessageAction(String text, String payload) {

    }
}
