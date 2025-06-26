package team.blackhole.bot.asky.handling.command;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;
import team.blackhole.bot.asky.handling.command.filtering.FilteringModule;
import team.blackhole.bot.asky.handling.command.message.handlers.NoneMessageHandler;
import team.blackhole.bot.asky.handling.command.message.handlers.NoneMessageHandlerHelper;

/**
 * Модуль для работы с командами
 */
public class CommandModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.install(new FilteringModule());

        binder.bind(NoneMessageHandlerHelper.class).in(Scopes.SINGLETON);
        binder.bind(NoneMessageHandler.class).in(Scopes.SINGLETON);
    }
}
