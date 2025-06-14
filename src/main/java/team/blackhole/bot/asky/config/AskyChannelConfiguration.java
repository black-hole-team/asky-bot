package team.blackhole.bot.asky.config;

import com.typesafe.config.Config;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import team.blackhole.bot.asky.channel.ChannelType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Конфигурация канала
 */
@Getter
public class AskyChannelConfiguration {

    /** Идентификатор канала */
    private final String id;

    /** Тип канала */
    private final ChannelType channelType;

    /** URL до вебхука, или {@code null}, если необходимо использовать long-pool */
    private final String webhookUrl;

    /** Признак необходимости использовать вебхук */
    private final boolean useWebhook;

    /** Параметры канала */
    private final Map<String, String> params;

    /**
     * Конструктор
     * @param channelId идентификатор канала
     * @param config    свойства
     */
    public AskyChannelConfiguration(String channelId, Config config) {
        id = channelId;
        channelType = ChannelType.valueOf(config.getString("type").toUpperCase());
        useWebhook = config.getBoolean("use_webhook");
        webhookUrl = config.getString("webhook_url");
        params = parseParams(config);
        assert !useWebhook || webhookUrl != null && !StringUtils.isEmpty(webhookUrl);
    }

    /**
     * Возвращает параметр по ключу
     * @param key ключ
     * @return значение параметра по ключу
     */
    public String getParam(String key) {
        return getParam(key, null);
    }

    /**
     * Возвращает параметр по ключу
     * @param key ключ
     * @param def значение по умолчанию
     * @return значение параметра по ключу
     */
    public String getParam(String key, String def) {
        return params.getOrDefault(key, def);
    }

    /**
     * Возвращает дополнительные параметры канала
     * @param config конфигурация канала
     * @return карта параметров
     */
    private static Map<String, String> parseParams(Config config) {
        var result = new HashMap<String, String>();
        for (var current : config.getConfig("params").entrySet()) {
            result.put(current.getKey(), String.valueOf(current.getValue().unwrapped()));
        }
        return Collections.unmodifiableMap(result);
    }
}
