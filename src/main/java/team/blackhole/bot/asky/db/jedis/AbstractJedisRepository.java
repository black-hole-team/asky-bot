package team.blackhole.bot.asky.db.jedis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;
import team.blackhole.bot.asky.support.exception.AskyException;

import java.time.Duration;
import java.util.Optional;

/**
 * Абстрактный репозиторий jedis
 * @param <T> тип сущности репозитория
 */
public abstract class AbstractJedisRepository<T> implements JedisRepository<T> {

    @Override
    public T save(String key, T entity) {
        return save(key, entity, null);
    }

    @Override
    public T save(String key, T entity, Duration duration) {
        try (var jedis = getJedisPool().getResource()) {
            var params = new SetParams();
            if (duration != null) {
                params.ex(duration.toMillis());
            }
            jedis.set(key, getObjectMapper().writeValueAsString(entity), params);
            return entity;
        } catch (JsonProcessingException e) {
            throw new AskyException("Ошибка при сохранении сущности '%s'".formatted(getPersistentClass()), e);
        }
    }

    @Override
    public Optional<T> findByKey(String key) {
        try (var jedis = getJedisPool().getResource()) {
            var found = jedis.get(key);
            if (found == null) {
                return Optional.empty();
            }
            return Optional.of(getObjectMapper().readValue(found, getPersistentClass()));
        } catch (JsonProcessingException e) {
            throw new AskyException("Ошибка при сохранении сущности '%s'".formatted(getPersistentClass()), e);
        }
    }

    /**
     * Возвращает пул jedis
     * @return пул jedis
     */
    protected abstract JedisPool getJedisPool();

    /**
     * Возвращает маппер объектов
     * @return маппер объектов
     */
    protected abstract ObjectMapper getObjectMapper();

    /**
     * Возвращает класс сущности репозитория
     * @return класс сущности репозитория
     */
    protected abstract Class<T> getPersistentClass();
}
