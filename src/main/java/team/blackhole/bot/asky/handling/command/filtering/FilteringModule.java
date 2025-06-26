package team.blackhole.bot.asky.handling.command.filtering;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;

/**
 * Модуль фильтрации
 */
public class FilteringModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(FilterHelperFactory.class).in(Scopes.SINGLETON);
        binder.bind(PageRendererFactoryProvider.class).in(Scopes.SINGLETON);
    }
}
