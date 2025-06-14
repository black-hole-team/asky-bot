package team.blackhole.bot.asky.db.hibernate;

import org.hibernate.SessionFactory;

import java.util.Optional;

/**
 * Реализация репозитория
 */
public abstract class AbstractHibernateRepository<T extends PersistentEntity, ID> implements HibernateRepository<T, ID> {

    @Override
    public T save(T entity) {
        var session = getSessionFactory().getCurrentSession();
        if (entity.isNew()) {
            session.persist(entity);
            return entity;
        } else {
            return session.merge(entity);
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(getSessionFactory().getCurrentSession().find(getPersistentClass(), id));
    }

    /**
     * Возвращает фабрику сессий
     * @return фабрика сессий
     */
    protected abstract SessionFactory getSessionFactory();

    /**
     * Возвращает класс сущности репозитория
     * @return класс сущности репозитория
     */
    protected abstract Class<T> getPersistentClass();
}
