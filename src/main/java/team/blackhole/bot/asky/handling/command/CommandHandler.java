package team.blackhole.bot.asky.handling.command;

import team.blackhole.bot.asky.db.jedis.domain.Stage;

/**
 * Обработчик команд
 */
public interface CommandHandler {

    /**
     * Обрабатывает команду и возвращает новую стадию
     * @param context контекст обработки сообщения
     * @return стадия обработки сообщения
     */
    Stage handle(CommandContext context);
}
