package team.blackhole.bot.asky.scheduling;

import com.google.inject.Inject;
import com.google.inject.Provider;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.jetbrains.annotations.NotNull;
import team.blackhole.bot.asky.db.hibernate.HibernateSessionContextUtils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

/**
 * Поставщик сервиса планировщика
 */
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class ScheduledExecutorServiceProvider implements Provider<ScheduledExecutorService> {

    /** Фабрика сессий */
    private final SessionFactory sessionFactory;

    @Override
    public ScheduledExecutorService get() {
        return Executors.newScheduledThreadPool(1, new ScheduledThreadFactory());
    }

    /**
     * Поток планировщика
     */
    private class ScheduledThread extends Thread {

        /**
         * Конструктор
         * @param runnable контракт на запуск метода в потоке
         */
        public ScheduledThread(Runnable runnable) {
            super(runnable);
        }

        @Override
        public void run() {
            try {
                super.run();
            } finally {
                HibernateSessionContextUtils.unbind((SessionFactoryImplementor) sessionFactory);
            }
        }
    }

    /**
     * Фабрика потоков планировщика
     */
    private class ScheduledThreadFactory implements ThreadFactory {

        @Override
        public Thread newThread(@NotNull Runnable r) {
            return new ScheduledThread(r);
        }
    }
}
