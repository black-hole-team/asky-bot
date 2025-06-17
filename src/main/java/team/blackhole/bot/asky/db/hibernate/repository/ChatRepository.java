package team.blackhole.bot.asky.db.hibernate.repository;

import team.blackhole.bot.asky.db.hibernate.HibernateRepository;
import team.blackhole.bot.asky.db.hibernate.domains.Chat;

import java.util.Optional;

/**
 * Репозиторий для работы с чатами
 */
public interface ChatRepository extends HibernateRepository<Chat, Long> {

    /**
     * Возвращает чат по идентификатору канала и идентификатору чата в канале
     * @param channelId     идентификатор канала
     * @param channelChatId идентификатор чата в канале
     * @return опциональное значение чата
     */
    Optional<Chat> findChatByChannelChatIdAndChannelId(String channelId, String channelChatId);
}
