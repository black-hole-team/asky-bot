package team.blackhole.bot.asky.service.hub_topic;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;

/**
 * Модуль сервиса для работы с темами хаба
 */
public class HubTopicServiceModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(HubTopicService.class).to(HubTopicServiceImpl.class).in(Scopes.SINGLETON);
    }
}
