package team.blackhole.bot.asky.executable;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;
import team.blackhole.bot.asky.scheduling.ScheduledExecutorServiceProvider;
import team.blackhole.bot.asky.scheduling.SchedulingService;

import java.util.concurrent.ScheduledExecutorService;

/**
 * Модуль исполняемых на стороне сценариев
 */
public class ExecutableModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(ExecutableFactory.class).to(ExecutableFactoryImpl.class).asEagerSingleton();
    }
}
