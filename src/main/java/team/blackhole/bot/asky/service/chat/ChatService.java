package team.blackhole.bot.asky.service.chat;

import team.blackhole.bot.asky.db.hibernate.domains.Chat;
import team.blackhole.bot.asky.db.hibernate.domains.ChatId;

/**
 * Сервис для работы с чатами
 */
public interface ChatService {

    /**
     * Возвращает чат по идентификатору или создает новый, если чат не найден
     * @param chatId идентификатор чата
     * @return найденный или созданный чат
     */
    Chat findByIdOrCreate(ChatId chatId);
}
