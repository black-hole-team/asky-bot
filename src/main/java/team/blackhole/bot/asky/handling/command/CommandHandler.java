package team.blackhole.bot.asky.handling.command;

import team.blackhole.bot.asky.db.jedis.domain.Stage;
import team.blackhole.bot.asky.handling.events.MessageEvent;
import team.blackhole.bot.asky.security.AskyUser;

/**
 * Обработчик команд
 */
public interface CommandHandler {

    /**
     * Обрабатывает стадию и возвращает новую
     * @param user  пользователь
     * @param stage стадия
     * @param event событие сообщения
     * @return стадия
     */
    Stage handle(AskyUser user, Stage stage, MessageEvent event);
}
