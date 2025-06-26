package team.blackhole.bot.asky.events.hooks;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;

/**
 * Модуль хуков
 */
public class HookModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(HookFactory.class).to(HookFactoryImpl.class).in(Scopes.SINGLETON);
        binder.bind(HookEngine.class).toProvider(HookEngineProvider.class).asEagerSingleton();
    }
}
