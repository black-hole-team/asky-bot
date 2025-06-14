package team.blackhole.bot.asky.support;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;

/**
 * Помощник по общим функциям приложения
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApplicationHelper {

    /** Русская локаль */
    public final static Locale RU = new Locale("RU-ru");

    /** Список профилей определенных в окружении запуска приложения */
    public final static Set<String> ENV = Set.of(System.getenv("ASKY_PROFILES") == null ?
            new String[0] : System.getenv("ASKY_PROFILES").split(","));

    /** Признак запуска в производственной среде */
    public final static boolean IS_PROD = ENV.contains("PROD");

    /**
     * Возвращает путь до домашней директории
     * @return путь до домашней директории
     */
    public static Path getHomePath() {
        return IS_PROD ? Path.of(System.getProperty("homePath")) : Path.of("distribution");
    }
}
