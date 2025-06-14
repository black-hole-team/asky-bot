package team.blackhole.bot.asky.config;

import com.typesafe.config.Config;
import lombok.Getter;

/**
 * Класс конфигурации базы данных
 */
@Getter
public class AskyDbConfiguration {

    /** URL соединения */
    private final String url;

    /** Драйвер */
    private final String driver;

    /** Имя пользователя */
    private final String username;

    /** Пароль */
    private final String password;

    /** Максимальный размер пула */
    private final int maxPoolSize;

    /** Размер пакета отправляемых данных */
    private final int batchSize;

    /**
     * Конструктор
     * @param config свойства
     */
    public AskyDbConfiguration(Config config) {
        url = config.getString("url");
        username = config.getString("user");
        driver = config.getString("driver");
        password = config.getString("password");
        maxPoolSize = config.getInt("max_pool_size");
        batchSize = config.getInt("batch_size");
    }
}
