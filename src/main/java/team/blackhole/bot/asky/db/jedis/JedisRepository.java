package team.blackhole.bot.asky.db.jedis;

import java.time.Duration;
import java.util.Optional;

/**
 * Абстрактный репозиторий
 * @param <T> тип сущности репозитория
 */
public interface JedisRepository<T> {

    /**
     * Сохраняет сущность
     * @param key    ключ
     * @param entity сущность для сохранения
     * @return сохраненная сущность
     */
    T save(String key, T entity);

    /**
     * Сохраняет сущность
     * @param key      ключ
     * @param entity   сущность для сохранения
     * @param duration продолжительность жизни значения по ключу
     * @return сохраненная сущность
     */
    T save(String key, T entity, Duration duration);

    /**
     * Возвращает сунщость по идентификатору
     * @param key ключ
     * @return найденная по идентификатору сущность
     */
    Optional<T> findByKey(String key);
}
