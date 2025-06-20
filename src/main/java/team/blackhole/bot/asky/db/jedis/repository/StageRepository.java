package team.blackhole.bot.asky.db.jedis.repository;

import team.blackhole.bot.asky.db.jedis.JedisRepository;
import team.blackhole.bot.asky.db.jedis.domain.Stage;

import java.util.Optional;

/**
 * Репозиторий для работы со стадией
 */
public interface StageRepository extends JedisRepository<Stage> {

    /**
     * Возвращает опциональное значение стадии по идентификатору канала идентификатору чата в канале и идентификатору пользователя
     * @param channelId     идентификатор канала
     * @param channelChatId идентификатор чата в канале
     * @return опциональное значение стадии
     */
    Optional<Stage> findByChannelTypeAndUserIdAndChatId(String channelId, String channelChatId);

    /**
     * Обновляет стадию по идентификатору канала идентификатору чата в канале и идентификатору пользователя
     * @param channelId     идентификатор канала
     * @param channelChatId идентификатор чата в канале
     * @param stage         новая стадия
     */
    void updateByChannelTypeAndUserIdAndChatId(String channelId, String channelChatId, Stage stage);
}
