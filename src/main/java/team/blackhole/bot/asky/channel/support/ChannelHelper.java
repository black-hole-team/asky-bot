package team.blackhole.bot.asky.channel.support;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import team.blackhole.bot.asky.channel.Channel;
import team.blackhole.bot.asky.channel.ChannelMessage;
import team.blackhole.bot.asky.channel.capability.ChatCapability;

/**
 * Хелпер для работы с каналами
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChannelHelper {

    /**
     * Отправляет ответ на сообщение в переданный канал
     * @param channel канал сообщения
     * @param message сообщение на которое должен быть отправлен ответ
     * @param content содержимое сообщения
     */
    public static void send(Channel channel, ChannelMessage message, String content) {
        // Отправляем сообщение об успешном создании хаба
        channel.getCapability(ChatCapability.class).ifPresent(chat -> chat.send(ChatCapability.MessageSending.builder()
                .chatId(message.chatId())
                .topicId(message.topicId())
                .content(content)
                .build()));
    }
}
