package team.blackhole.bot.asky.config;

import com.typesafe.config.Config;
import lombok.Getter;

/**
 * Конфигурация хаба обработки обращений
 */
@Getter
public class AskyHubConfiguration {

    /** Шаблон наименования субъекта обращения */
    private final String subjectNamePattern;

    /**
     * Конструктор
     * @param config свойства
     */
    public AskyHubConfiguration(Config config) {
        subjectNamePattern = config.getString("subject_name_pattern");
    }
}