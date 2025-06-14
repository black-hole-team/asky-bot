package team.blackhole.bot.asky.providers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Provider;

/**
 * Поставщик маппер объектов
 */
public class ObjectMapperProvider implements Provider<ObjectMapper> {

    @Override
    public ObjectMapper get() {
        return new ObjectMapper();
    }
}
