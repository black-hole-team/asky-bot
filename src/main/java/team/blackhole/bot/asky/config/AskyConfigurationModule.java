package team.blackhole.bot.asky.config;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import team.blackhole.bot.asky.support.ApplicationHelper;

/**
 * Предоставляет реализацию свойств в инжектор guice
 */
public class AskyConfigurationModule implements Module {

    /** Наименование файла с конфигурацией */
    private static final String CONFIG_FILENAME = "application.conf";

    @Override
    public void configure(Binder binder) {
        var resolved = getConfig();

        binder.bind(AskyDbConfiguration.class).toInstance(new AskyDbConfiguration(resolved.getConfig("db")));
        binder.bind(AskyChannelsConfiguration.class).toInstance(new AskyChannelsConfiguration(resolved.getConfig("channels")));
        binder.bind(AskyWebhookConfiguration.class).toInstance(new AskyWebhookConfiguration(resolved.getConfig("webhook")));
        binder.bind(AskyRedisConfiguration.class).toInstance(new AskyRedisConfiguration(resolved.getConfig("redis")));
        binder.bind(AskyHubConfiguration.class).toInstance(new AskyHubConfiguration(resolved.getConfig("hub")));
        binder.bind(AskyHandlingConfiguration.class).toInstance(new AskyHandlingConfiguration(resolved.getConfig("handling")));
        binder.bind(AskyExecutableConfiguration.class).toInstance(new AskyExecutableConfiguration(resolved.getConfig("executable")));
    }

    /**
     * Возвращает конфигурацию приложения
     * @return конфигурация приложения
     */
    private static Config getConfig() {
        return ConfigFactory.parseFile(ApplicationHelper.getHomePath().resolve("config").resolve(CONFIG_FILENAME).toFile())
                .resolveWith(ConfigFactory.parseProperties(System.getProperties()));
    }
}
