package team.blackhole.bot.asky.db.hibernate;

import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Обработчик аннотации {@link Transactional}
 */
@RequiredArgsConstructor
public class HibernateTransactionalListener implements TypeListener {

    private final Provider<SessionFactory> sessionFactoryProvider;

    @Override
    public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
        if (Arrays.stream(type.getRawType().getMethods()).noneMatch(m -> m.isAnnotationPresent(Transactional.class))) {
            return;
        }

        encounter.bindInterceptor(
            Matchers.annotatedWith(Transactional.class),
            invocation -> {
                var sessionFactory = sessionFactoryProvider.get();
                var session = sessionFactory.getCurrentSession();
                var isNewTransaction = session.getTransaction() == null || !session.getTransaction().isActive();
                var transaction = isNewTransaction ? session.beginTransaction() : null;

                try {
                    var result = invocation.proceed();
                    if (isNewTransaction) {
                        transaction.commit();
                    }
                    return result;
                } catch (Throwable ex) {
                    if (isNewTransaction && shouldRollback(invocation.getMethod(), ex)) {
                        try {
                            transaction.rollback();
                        } catch (Exception rollbackEx) {
                            ex.addSuppressed(rollbackEx);
                        }
                    }
                    throw ex;
                } finally {
                    if (isNewTransaction) {
                        session.clear();
                    }
                }
            }
        );
    }

    /**
     * Возвращает признак возможности отката транзакции
     * @param method метод, который попадает под транзакцию
     * @param ex     исключение выброшенное в процессе работы метода
     * @return {@code true}, если необходимо выполнить откат транзакции и {@code false}, если иначе
     */
    private boolean shouldRollback(Method method, Throwable ex) {
        var transactional = method.getAnnotation(Transactional.class);

        if (transactional == null) {
            // Если аннотации нет, откатываем только на RuntimeException и Error
            return ex instanceof RuntimeException || ex instanceof Error;
        }

        // Проверяем noRollbackFor (исключения, которые НЕ должны приводить к откату)
        for (var noRollbackEx : transactional.dontRollbackOn()) {
            if (noRollbackEx.isInstance(ex)) {
                return false;
            }
        }

        // Проверяем rollbackFor (исключения, которые ДОЛЖНЫ приводить к откату)
        for (var rollbackEx : transactional.rollbackOn()) {
            if (rollbackEx.isInstance(ex)) {
                return true;
            }
        }

        // Если ничего не указано, откатываем только RuntimeException и Error
        return ex instanceof RuntimeException || ex instanceof Error;
    }
}