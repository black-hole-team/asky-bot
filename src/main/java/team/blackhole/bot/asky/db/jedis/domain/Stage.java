package team.blackhole.bot.asky.db.jedis.domain;

import team.blackhole.bot.asky.handling.command.StageName;

import java.util.Map;

/**
 * Стадия общения с ботом
 * @param name        наименование стадии
 * @param data        данные стадии
 * @param propagation признак необходимости распостронения стадии в текущей сессии
 */
public record Stage(StageName name, Map<String, String> data, boolean propagation) {

    /**
     * Возвращает стадию с переключеным флагом распостранения
     * @param stage       исходная стадия
     * @param propagation признак необходимости распостронения стадии в текущей сессии
     * @return стадия
     */
    public static Stage propagation(Stage stage, boolean propagation) {
        return new Stage(stage.name, stage.data, propagation);
    }
}
