package team.blackhole.bot.asky.db.hibernate;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionFactoryImplementor;

import java.util.HashMap;
import java.util.Map;

/**
 * Инструменты для работы с сессиями
 */
@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HibernateSessionContextUtils {

    /** Текущая сессия */
    private static final Map<String, ThreadLocal<Session>> FACTORY_UUID_TO_CURRENT_SESSION = new HashMap<>();

    /**
     * Создает сессию или возвращает уже созданную
     * @param sessionFactory фабрика сессий
     */
    public static Session createSession(SessionFactoryImplementor sessionFactory) {
        var currentSession = FACTORY_UUID_TO_CURRENT_SESSION.computeIfAbsent(sessionFactory.getUuid(), uuid -> new ThreadLocal<>());
        var session = currentSession.get();
        if (session == null || !session.isOpen()) {
            session = sessionFactory.openSession();
            currentSession.set(session);
        }
        return session;
    }

    /**
     * Закрыть текущую сессию
     */
    public static void unbind(SessionFactoryImplementor sessionFactory) {
        var currentSession = FACTORY_UUID_TO_CURRENT_SESSION.get(sessionFactory.getUuid());
        if (currentSession == null) {
            return;
        }
        var session = currentSession.get();
        if (session != null) {
            try {
                if (session.isOpen()) {
                    var transaction = session.getTransaction();
                    if (transaction != null && transaction.isActive()) {
                        transaction.rollback();
                    }
                    session.close();
                }
            } catch (Exception e) {
                log.error("Ошибка при закрытии фабрики сессий", e);
            } finally {
                currentSession.remove();
            }
        }
    }
}
