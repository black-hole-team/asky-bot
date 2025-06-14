package team.blackhole.bot.asky.handling.stage;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;
import team.blackhole.bot.asky.handling.stage.handlers.NoneStageHandler;

/**
 * Модуль менеджера состояний
 */
public class StageModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(NoneStageHandler.class).in(Scopes.SINGLETON);
        binder.bind(StageManager.class).toProvider(StageManagerProvider.class).in(Scopes.SINGLETON);
    }
}
