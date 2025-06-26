package team.blackhole.bot.asky.handling;

import com.google.inject.Binder;
import com.google.inject.Module;
import team.blackhole.bot.asky.handling.command.CommandModule;

/**
 * Модуль обработчиков
 */
public class HandlersModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.install(new CommandModule());
        binder.bind(HandlingEventListener.class).toProvider(HandlingEventListenerProvider.class).asEagerSingleton();
    }
}
