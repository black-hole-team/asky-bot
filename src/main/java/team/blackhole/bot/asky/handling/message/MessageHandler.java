package team.blackhole.bot.asky.handling.message;

import team.blackhole.bot.asky.channel.ChannelMessage;
import team.blackhole.bot.asky.db.jedis.domain.Stage;

/**
 * Обработчик сообщений
 */
public interface MessageHandler {

    /**
     * Обрабатывает новое сообщение канала
     * @param stage   стадия обработки
     * @param message сообщение
     * @return новая стадия обработки сообщений
     */
    Stage handle(Stage stage, ChannelMessage message);
}
