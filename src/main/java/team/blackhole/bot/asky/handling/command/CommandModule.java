package team.blackhole.bot.asky.handling.command;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;
import team.blackhole.bot.asky.handling.command.handlers.NoneHandler;

/**
 * Модуль обработки команд
 */
public class CommandModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(NoneHandler.class).in(Scopes.SINGLETON);
        binder.bind(CommandHandlerManager.class).toProvider(CommandHandlerManagerProvider.class).in(Scopes.SINGLETON);
    }
}
