package team.blackhole.bot.asky.queue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import redis.clients.jedis.JedisPool;
import team.blackhole.bot.asky.support.exception.AskyException;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Реализация отложенной очереди основанной на redis
 */
@RequiredArgsConstructor
public abstract class RedisDelayedQueue<T> implements DelayedQueue<T> {

    /** Ключ очереди */
    private final String queueKey;

    @Override
    public void add(T element, Duration delay) {
        try (var jedis = getJedisPool().getResource()) {
            jedis.zadd(queueKey, getSecond() + delay.getSeconds(), getObjectMapper().writeValueAsString(element));
        } catch (JsonProcessingException e) {
            throw new AskyException("Ошибка при попытки добавления значения в пул", e);
        }
    }

    @Override
    public void add(T element, TemporalAccessor temporal) {
        try (var jedis = getJedisPool().getResource()) {
            jedis.zadd(queueKey, Instant.from(temporal).getEpochSecond(), getObjectMapper().writeValueAsString(element));
        } catch (JsonProcessingException e) {
            throw new AskyException("Ошибка при попытки добавления значения в пул", e);
        }
    }

    @Override
    public List<T> pollExpired() {
        try (var jedis = getJedisPool().getResource()) {
            var now = getSecond();
            var expired = jedis.zrangeByScore(queueKey, 0, now);
            if (expired.isEmpty()) {
                return Collections.emptyList();
            } else {
                // Атомарное удаление обработанных элементов
                jedis.zremrangeByScore(queueKey, 0, now);
            }
            var result = new ArrayList<T>();
            var objectMapper = getObjectMapper();
            for (var item : expired) {
                result.add(objectMapper.readValue(item, getEntityClass()));
            }
            return result;
        } catch (JsonProcessingException e) {
            throw new AskyException("Ошибка при попытки добавления значения в пул", e);
        }
    }

    /**
     * Возвращает количество секунд
     * @return количество секунд
     */
    private static long getSecond() {
        return Instant.now().getEpochSecond();
    }

    /**
     * Возвращает класс сущности очереди
     * @return класс сущности очереди
     */
    abstract protected Class<T> getEntityClass();

    /**
     * Возвращает маппер объектов
     * @return маппер объектов
     */
    abstract protected ObjectMapper getObjectMapper();

    /**
     * Возвращает пул jedis
     * @return пул jedis
     */
    abstract protected JedisPool getJedisPool();
}