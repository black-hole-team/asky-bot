package team.blackhole.bot.asky.service;

import team.blackhole.bot.asky.db.hibernate.HibernateRepository;
import team.blackhole.bot.asky.db.hibernate.PersistentEntity;
import team.blackhole.bot.asky.db.support.Page;
import team.blackhole.bot.asky.support.exception.AskyException;
import team.blackhole.data.filter.Filter;

import java.util.Optional;

/**
 * Сервис для работы с сущностями hibernate
 * @param <T>  тип сущности
 * @param <ID> тип идентификатора
 */
public interface HibernateService<T extends PersistentEntity, ID> {

    /**
     * Возвращает репозиторий сервиса
     * @return репозиторий сервиса
     */
    HibernateRepository<T, ID> getRepository();

    /**
     * Удаляет сущность по идентификатору
     * @param id идентификатор
     */
    default void delete(ID id) {
        getRepository().delete(id);
    }

    /**
     * Возвращает обращение по идентификатору
     * @param id идентификатор обращения
     * @return найденное обращение
     */
    default Optional<T> findById(ID id) {
        return getRepository().findById(id);
    }

    /**
     * Возвращает обращение по идентификатору
     * @param id идентификатор обращения
     * @return найденное обращение
     */
    default T findByIdOrThrow(ID id) {
        return findById(id).orElseThrow(() -> new AskyException("Невозможно найти сущность по идентификатору '%s'".formatted(id)));
    }

    /**
     * Возвращает страницу обращений по фильтру
     * @param filter фильтр
     * @return страница обращений по фильтру
     */
    default Page<T> findAll(Filter filter) {
        return getRepository().findAll(filter);
    }
}
