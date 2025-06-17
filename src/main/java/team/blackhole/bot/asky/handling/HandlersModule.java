package team.blackhole.bot.asky.handling;

import com.google.inject.Binder;
import com.google.inject.Module;
import team.blackhole.bot.asky.handling.command.CommandModule;
import team.blackhole.bot.asky.handling.hooks.HookModule;

/**
 * Модуль обработчиков
 */
public class HandlersModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.install(new HookModule());
        binder.install(new CommandModule());

        binder.bind(MessageEventListener.class).toProvider(MessageEventListenerProvider.class).asEagerSingleton();
    }
}
