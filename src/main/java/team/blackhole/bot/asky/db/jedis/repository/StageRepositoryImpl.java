package team.blackhole.bot.asky.db.jedis.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;
import redis.clients.jedis.JedisPool;
import team.blackhole.bot.asky.db.jedis.AbstractJedisRepository;
import team.blackhole.bot.asky.db.jedis.domain.Stage;

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
}
