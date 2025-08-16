package team.blackhole.bot.asky.executable;

import java.nio.file.Path;

/**
 * Фабрика исполняемых действий
 */
public interface ExecutableFactory {

    /**
     * Создает исполняемое действие
     * @param path путь до файла со сценарием этого действия
     * @param name имя члена сценария, которое будет вызвано
     * @return созданное исполняемое действие
     */
    Executable create(Path path, String name);
}
