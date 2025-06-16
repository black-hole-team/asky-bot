package team.blackhole.bot.asky.config;

import com.typesafe.config.Config;
import lombok.Getter;

/**
 * Класс конфигурации вебхуков
 */
@Getter
public class AskyWebhookConfiguration {

    /** Базовый url для приёма вебхуков */
    private final String base;

    /** Порт для приёма вебхуков */
    private final int port;

    /** Хост */
    private final String host;

    /** Конфигурация SSL */
    private final AskyWebhookSSLConfiguration ssl;

    /**
     * Конструктор
     * @param config свойства
     */
    public AskyWebhookConfiguration(Config config) {
        base = config.getString("base");
        port = config.getInt("port");
        host = config.getString("host");
        if (config.hasPath("ssl")) {
            ssl = new AskyWebhookSSLConfiguration(config.getConfig("ssl"));
        } else {
            ssl = null;
        }
    }
}
