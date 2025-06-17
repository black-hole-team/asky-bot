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
     * Возвращает информацию о пользователя чата
     * @param chatId идентификатор чата
     * @param userId идентификатор пользователя
     */
    ChatUserInfo getChatUserInfo(String chatId, long userId);

    /**
     * Тип канала, по которому сообщение было получено
     * @param chatId      идентификатор чата
     * @param topicId     идентификатор темы
     * @param replyTo     идентификатор сообщения для ответа
     * @param content     содержимое сообщения
     * @param attachments вложения сообщения
     */
    @Builder
    record MessageSending(String chatId, String topicId, Long replyTo, String content, List<ChannelAttachment> attachments) {
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
}
