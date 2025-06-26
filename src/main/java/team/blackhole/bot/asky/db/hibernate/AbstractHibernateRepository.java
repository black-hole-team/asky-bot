package team.blackhole.bot.asky.db.hibernate;

import lombok.extern.log4j.Log4j2;
import org.hibernate.SessionFactory;
import org.hibernate.query.sqm.tree.select.SqmSelectStatement;
import team.blackhole.bot.asky.db.support.Page;
import team.blackhole.bot.asky.db.support.PageImpl;
import team.blackhole.data.filter.Filter;

import java.util.Collections;
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

    @Override
    public Page<T> findAll(Filter filter) {
        var session = getSessionFactory().getCurrentSession();
        var query = (SqmSelectStatement<T>) new HibernateFilterConverter<>(session, getPersistentClass()).convert(filter);
        var count = session.createQuery(query.createCountQuery()).getSingleResult();
        if (count == 0) {
            return new PageImpl<>(Collections.emptyList(), filter.getPage(), 0, filter);
        }
        return new PageImpl<>(session.createQuery(query)
                .setMaxResults(filter.getPageSize())
                .setFirstResult(filter.getPage() * filter.getPageSize())
                .getResultList(), filter.getPage(), count, filter);
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
