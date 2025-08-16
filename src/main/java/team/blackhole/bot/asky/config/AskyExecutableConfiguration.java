package team.blackhole.bot.asky.config;

import com.typesafe.config.Config;
import lombok.Getter;
import team.blackhole.bot.asky.support.ApplicationHelper;

import java.nio.file.Path;

/**
 * Конфигурация исполняемых на стороне сценариев
 */
@Getter
public class AskyExecutableConfiguration {

    /** Директория, где хранятся исполняемые файлы */
    private final Path dir;

    /** Конфигурация исполняемых на стороне сценариев хуков */
    private final AskyExecutableHooksConfiguration hooks;

    /**
     * Конструктор
     * @param config конфигурация
     */
    public AskyExecutableConfiguration(Config config) {
        dir = ApplicationHelper.getHomePath().resolve(config.getString("dir"));
        hooks = new AskyExecutableHooksConfiguration(dir, config.getConfig("hooks"));
    }
}
