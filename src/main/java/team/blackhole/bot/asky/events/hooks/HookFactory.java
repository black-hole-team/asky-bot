package team.blackhole.bot.asky.events.hooks;

import java.nio.file.Path;

/**
 * Фабрика хуков
 */
public interface HookFactory {

    /**
     * Создает реализацию хука на основе обработчика
     * @param pathToSourceFile путь до файла обработчика
     * @return созданный хук
     */
    Hook create(Path pathToSourceFile);
}
