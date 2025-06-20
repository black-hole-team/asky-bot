package team.blackhole.bot.asky.handling.message.command;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;
import team.blackhole.bot.asky.handling.message.command.handlers.NoneHandler;
import team.blackhole.bot.asky.handling.message.command.handlers.NoneHandlerHelper;

/**
 * Модуль для работы с командами
 */
public class CommandModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(NoneHandlerHelper.class).in(Scopes.SINGLETON);
        binder.bind(NoneHandler.class).in(Scopes.SINGLETON);
    }
}
