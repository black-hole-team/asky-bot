package team.blackhole.bot.asky.db.hibernate;

import lombok.extern.log4j.Log4j2;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

/**
 * Реализация репозитория
 */
@Log4j2
public abstract class AbstractHibernateRepository<T extends PersistentEntity, ID> implements HibernateRepository<T, ID> {

    @Override
    public T save(T entity) {
        var session = getSessionFactory().getCurrentSession();
        if (entity.isNew()) {
            session.persist(entity);
            log.info(entity);
            return entity;
        } else {
            return session.merge(entity);
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(getSessionFactory().getCurrentSession().find(getPersistentClass(), id));
    }

    @Override
    public List<T> findAllById(List<ID> ids) {
        return getSessionFactory().getCurrentSession().findMultiple(getPersistentClass(), ids);
    }

    @Override
    public void delete(ID id) {
        var session = getSessionFactory().getCurrentSession();
        session.remove(session.find(getPersistentClass(), id));
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
