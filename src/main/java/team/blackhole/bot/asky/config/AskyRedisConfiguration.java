package team.blackhole.bot.asky.config;

import com.typesafe.config.Config;
import lombok.Getter;

/**
 * Конфигурация подключения к redis
 */
@Getter
public class AskyRedisConfiguration {

    /** Хост для подключения к redis */
    private final String host;

    /** Порт для подключения к redis */
    private final int port;

    /**
     * Конструктор
     * @param config свойства
     */
    public AskyRedisConfiguration(Config config) {
        host = config.getString("host");
        port = config.getInt("port");
    }
}
