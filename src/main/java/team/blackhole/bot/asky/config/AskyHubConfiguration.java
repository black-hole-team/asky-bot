package team.blackhole.bot.asky.config;

import com.typesafe.config.Config;
import lombok.Getter;

import java.time.Duration;
import java.time.ZoneId;

/**
 * Конфигурация хаба обработки обращений
 */
@Getter
public class AskyHubConfiguration {

    /** Шаблон наименования субъекта обращения */
    private final String subjectNamePattern;

    /** Таймаут удаления топика, тема которого была закрыта */
    private final Duration deleteTopicTimeout;

    /** Временная зона */
    private final ZoneId timezone;

    /**
     * Конструктор
     * @param config свойства
     */
    public AskyHubConfiguration(Config config) {
        subjectNamePattern = config.getString("subject_name_pattern");
        deleteTopicTimeout = "false".equals(config.getString("delete_topic_timeout")) ? null : config.getDuration("delete_topic_timeout");
        timezone = ZoneId.of(config.getString("timezone"));
    }
}