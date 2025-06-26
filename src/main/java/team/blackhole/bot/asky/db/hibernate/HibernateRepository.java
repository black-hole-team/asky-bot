package team.blackhole.bot.asky.db.hibernate;

import team.blackhole.bot.asky.db.support.Page;
import team.blackhole.data.filter.Filter;

import java.util.List;
import java.util.Optional;

/**
 * Абстрактный репозиторий
 * @param <T> тип сущности репозитория
 */
public interface HibernateRepository<T extends PersistentEntity, ID> {

    /**
     * Удаляет сущность по идентификатору
     * @param id идентификатор сущности
     */
    void delete(ID id);

    /**
     * Сохраняет сущность
     * @param entity сущность для сохранения
     * @return сохраненная сущность
     */
    T save(T entity);

    /**
     * Возвращает страницу по фильтру
     * @param filter фильтр для поиска сущностей
     * @return страница сущностей по фильтру
     */
    Page<T> findAll(Filter filter);

    /**
     * Возвращает сущность по идентификатору
     * @param id идентификатор
     * @return найденная по идентификатору сущность
     */
    Optional<T> findById(ID id);

    /**
     * Возвращает список сущностей по идентификаторам
     * @param ids идентификаторы сущностей
     * @return список сущностей по идентификаторам
     */
    List<T> findAllById(List<ID> ids);
}
