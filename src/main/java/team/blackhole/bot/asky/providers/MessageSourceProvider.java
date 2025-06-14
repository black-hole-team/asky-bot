package team.blackhole.bot.asky.providers;

import com.google.inject.Provider;
import team.blackhole.bot.asky.support.ApplicationHelper;
import team.blackhole.bot.asky.support.MessageSource;
import team.blackhole.bot.asky.support.MessageSourceImpl;
import team.blackhole.bot.asky.support.PropertiesLoaderImpl;

/**
 * Поставщик источника сообщений
 */
public class MessageSourceProvider implements Provider<MessageSource> {

    /** Время кеширования 30 минут */
    private static final long CACHE_TIME = 1000 * 60 * 30;

    @Override
    public MessageSource get() {
        return new MessageSourceImpl(ApplicationHelper.RU, "messages", CACHE_TIME,
                new PropertiesLoaderImpl(ApplicationHelper.getHomePath().resolve("messages")));
    }
}
