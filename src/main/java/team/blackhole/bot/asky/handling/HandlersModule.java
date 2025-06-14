package team.blackhole.bot.asky.handling;

import com.google.inject.Binder;
import com.google.inject.Module;
import team.blackhole.bot.asky.handling.hooks.HookModule;
import team.blackhole.bot.asky.handling.stage.StageModule;

/**
 * Модуль обработчиков
 */
public class HandlersModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.install(new HookModule());
        binder.install(new StageModule());

        binder.bind(MessageEventListener.class).toProvider(MessageEventListenerProvider.class).asEagerSingleton();
    }
}
