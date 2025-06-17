package team.blackhole.bot.asky.providers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.EventBus;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;
import org.eclipse.jetty.client.HttpClient;
import team.blackhole.bot.asky.support.MessageSource;

/**
 * Модуль поставщиков сервисных классов
 */
public class ProvidersModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(EventBus.class).toInstance(new EventBus());
        binder.bind(HttpClient.class).toProvider(HttpClientProvider.class).in(Scopes.SINGLETON);
        binder.bind(ObjectMapper.class).toProvider(ObjectMapperProvider.class).in(Scopes.SINGLETON);
        binder.bind(MessageSource.class).toProvider(MessageSourceProvider.class).in(Scopes.SINGLETON);
    }
}
