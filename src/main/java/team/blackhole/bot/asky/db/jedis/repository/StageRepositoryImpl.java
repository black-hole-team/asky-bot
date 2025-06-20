package team.blackhole.bot.asky.db.jedis.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;
import redis.clients.jedis.JedisPool;
import team.blackhole.bot.asky.db.jedis.AbstractJedisRepository;
import team.blackhole.bot.asky.db.jedis.domain.Stage;

import java.util.Optional;

/**
 * Реализация репозитория {@link StageRepository}
 */
@RequiredArgsConstructor(onConstructor_ = @__(@Inject))
public class StageRepositoryImpl extends AbstractJedisRepository<Stage> implements StageRepository {

    /** Маппер объектов */
    private final ObjectMapper objectMapper;

    /** Пул jedis */
    private final JedisPool jedisPool;

    @Override
    protected JedisPool getJedisPool() {
        return jedisPool;
    }

    @Override
    protected ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    @Override
    protected Class<Stage> getPersistentClass() {
        return Stage.class;
    }

    @Override
    public Optional<Stage> findByChannelTypeAndUserIdAndChatId(String channelId, String channelChatId) {
        return findByKey(getKey(channelId, channelChatId));
    }

    @Override
    public void updateByChannelTypeAndUserIdAndChatId(String channelId, String channelChatId, Stage stage) {
        save(getKey(channelId, channelChatId), stage);
    }

    /**
     * Возвращает ключ стадии в регистре redis
     * @param channelId     идентификатор канала
     * @param channelChatId идентификатор чата в канале
     * @return ключ в регистре redis
     */
    private String getKey(String channelId, String channelChatId) {
        return "%s.%s".formatted(channelId, channelChatId);
    }
}
