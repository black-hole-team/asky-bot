package team.blackhole.bot.asky.config;

import com.google.inject.Inject;
import com.typesafe.config.Config;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import team.blackhole.bot.asky.support.ApplicationHelper;

import java.nio.file.Path;

/**
 * Конфигурация исполняемых файлов хуков
 */
@Getter
public class AskyExecutableHooksConfiguration {

    /** Директория, где хранятся исполняемые файлы хуков */
    private final Path dir;

    /**
     * Конструктор
     * @param config конфигурация
     */
    public AskyExecutableHooksConfiguration(Path executableDir, Config config) {
        dir = executableDir.resolve(config.getString("dir"));
    }
}
