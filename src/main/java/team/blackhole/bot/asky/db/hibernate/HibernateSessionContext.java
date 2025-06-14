package team.blackhole.bot.asky.db.hibernate;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.context.spi.CurrentSessionContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;

/**
 * Контекст текущей сессии
 */
@RequiredArgsConstructor
public class HibernateSessionContext implements CurrentSessionContext {

    /** Фабрика сессий */
    private final SessionFactoryImplementor sessionFactory;

    @Override
    public Session currentSession() {
        return HibernateSessionContextUtils.createSession(sessionFactory);
    }
}