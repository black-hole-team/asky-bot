package team.blackhole.bot.asky.handling.message;

import com.google.inject.Binder;
import com.google.inject.Module;
import team.blackhole.bot.asky.handling.message.command.CommandModule;

/**
 * Модуль обработки сообщений
 */
public class MessageModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.install(new CommandModule());
        binder.bind(MessageEventListener.class).toProvider(MessageEventListenerProvider.class).asEagerSingleton();
    }
}
