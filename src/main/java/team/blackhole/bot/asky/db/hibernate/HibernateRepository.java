package team.blackhole.bot.asky.db.hibernate;

import java.util.Optional;

/**
 * Абстрактный репозиторий
 * @param <T> тип сущности репозитория
 */
public interface HibernateRepository<T extends PersistentEntity, ID> {

    /**
     * Сохраняет сущность
     * @param entity сущность для сохранения
     * @return сохраненная сущность
     */
    T save(T entity);

    /**
     * Возвращает сунщость по идентификатору
     * @param id идентификатор
     * @return найденная по идентификатору сущность
     */
    Optional<T> findById(ID id);
}
