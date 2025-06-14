package team.blackhole.bot.asky.db.jedis.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import team.blackhole.bot.asky.config.AskyRedisConfiguration;

import java.time.Duration;

/**
 * Поставщик пула соединений с redis
 */
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class JedisPoolProvider implements Provider<JedisPool> {

    /** Свойства приложения */
    private final AskyRedisConfiguration configuration;

    @Override
    public JedisPool get() {
        log.info("Соединяюсь с redis {}:{}", configuration.getHost(), configuration.getPort());
        return new JedisPool(buildPoolConfig(), configuration.getHost(), configuration.getPort());
    }

    /**
     * Возвращает конфигурацию пула соединений redis
     * @return конфигурацию пула соединений redis
     */
    private static JedisPoolConfig buildPoolConfig() {
        var poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128);
        poolConfig.setMaxIdle(128);
        poolConfig.setMinIdle(16);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setMinEvictableIdleDuration(Duration.ofSeconds(60));
        poolConfig.setTimeBetweenEvictionRuns(Duration.ofSeconds(30));
        poolConfig.setNumTestsPerEvictionRun(3);
        poolConfig.setBlockWhenExhausted(true);
        return poolConfig;
    }
}
