package team.blackhole.bot.asky.events;

import com.google.inject.Binder;
import com.google.inject.Module;
import team.blackhole.bot.asky.events.hooks.HookModule;

/**
 * Модуль обработчиков
 */
public class EventsModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.install(new HookModule());
    }
}
