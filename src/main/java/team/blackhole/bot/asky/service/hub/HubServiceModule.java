package team.blackhole.bot.asky.service.hub;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;

/**
 * Модуль сервиса хабов
 */
public class HubServiceModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(HubService.class).to(HubServiceImpl.class).in(Scopes.SINGLETON);
    }
}
