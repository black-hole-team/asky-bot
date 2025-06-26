package team.blackhole.bot.asky.service.chat;

import team.blackhole.bot.asky.db.hibernate.domains.Chat;
import team.blackhole.bot.asky.service.HibernateService;
import team.blackhole.bot.asky.service.chat.data.CreateChatData;

import java.util.Optional;

/**
 * Сервис для работы с чатами
 */
public interface ChatService extends HibernateService<Chat, Long> {

    /**
     * Создает новый чат
     * @param data данные для создания чата
     * @return созданный чат
     */
    Chat create(CreateChatData data);

    /**
     * Возвращает чат по идентификатору канала и идентификатору чата в канале
     * @param channelId     идентификатор канала
     * @param channelChatId идентификатор чата в канале
     * @return опциональное значение чата
     */
    Optional<Chat> findChatByChannelChatIdAndChannelId(String channelId, String channelChatId);
}
