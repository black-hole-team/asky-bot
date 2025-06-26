package team.blackhole.bot.asky.db.jedis.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import team.blackhole.bot.asky.handling.StageName;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Стадия общения с ботом
 * @param name        наименование стадии
 * @param data        данные стадии
 * @param prev        предыдущая стадия или {@code null}
 * @param propagation признак необходимости распространения стадии в текущей сессии
 */
public record Stage(StageName name, Map<String, String> data, Stage prev, @JsonIgnore boolean propagation) {

    /**
     * Возвращает данные по ключу key
     * @param key ключ данных
     * @return данные по ключу
     */
    public Optional<String> getData(String key) {
        return Optional.ofNullable(data.get(key));
    }

    /**
     * Устанавливает данные по ключу key
     * @param key   ключ данных
     * @param value значение
     */
    public void setData(String key, String value) {
        data.put(key, value);
    }

    /**
     * Удаляет данные по ключу
     * @param key ключ данных
     */
    public void removeData(String key) {
        data.remove(key);
    }

    /**
     * Возвращает стадию с переключенным флагом распространения
     * @param stage       исходная стадия
     * @param propagation признак необходимости распространения стадии в текущей сессии
     * @return стадия
     */
    public static Stage propagation(Stage stage, boolean propagation) {
        return new Stage(stage.name, new HashMap<>(stage.data), stage.prev == null ? null : Stage.propagation(stage.prev, propagation), propagation);
    }
}
