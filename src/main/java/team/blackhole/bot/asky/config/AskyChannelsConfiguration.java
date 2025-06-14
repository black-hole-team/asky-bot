package team.blackhole.bot.asky.config;

import com.typesafe.config.Config;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Конфигурация каналов
 */
@Getter
public class AskyChannelsConfiguration {

    /** Карта, где ключ это идентификатор канала, а значение это его конфигурация */
    private final Map<String, AskyChannelConfiguration> channels;

    /**
     * Конструктор
     * @param config свойства
     */
    public AskyChannelsConfiguration(Config config) {
        channels = parseList(config.getConfig("list"));
    }

    /**
     * Возвращает признак наличия канала используюего веб-хуки
     * @return {@code true}, если есть канал использующий веб-хуки {@code false}, если иначе
     */
    public boolean hasWebhookChannel() {
        return channels.values().stream().anyMatch(AskyChannelConfiguration::isUseWebhook);
    }

    /**
     * Разбирает список каналов
     * @param config конфигурация со списком каналов
     * @return карта, где ключ это идентификатор канала, а значение это его конфигурация
     */
    @NotNull
    private static Map<String, AskyChannelConfiguration> parseList(Config config) {
        return config.root().keySet().stream().collect(Collectors.toMap(Function.identity(),
                key -> new AskyChannelConfiguration(key, config.getConfig("%s".formatted(key)))));
    }
}
