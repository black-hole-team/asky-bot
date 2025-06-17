package team.blackhole.bot.asky.scheduling;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;

import java.util.concurrent.ScheduledExecutorService;

/**
 * Модуль задач, выполняемых по расписанию
 */
public class SchedulingModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(ScheduledExecutorService.class).toProvider(ScheduledExecutorServiceProvider.class).in(Scopes.SINGLETON);
        binder.bind(SchedulingService.class).in(Scopes.SINGLETON);
    }
}
