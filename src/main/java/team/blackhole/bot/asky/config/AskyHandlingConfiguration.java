package team.blackhole.bot.asky.config;

import com.typesafe.config.Config;
import lombok.Getter;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Конфигурация обработки сообщений
 */
@Getter
public class AskyHandlingConfiguration {

    /** Количество потоков для обработки сообщений или -1, если значение должно принять количество потоков системы */
    private final int handlingThreadsCount;

    /** Конфигурация роутинга */
    private final Map<String, Set<String>> routing;

    /** Конфигурация ролей пользователей */
    private final Map<String, ChannelRolesConfiguration> roles;

    /**
     * Конструктор
     * @param config свойства
     */
    public AskyHandlingConfiguration(Config config) {
        handlingThreadsCount = config.getInt("handling_threads_count");
        routing = parseRouting(config.getConfig("routing"));
        roles = parseRoles(config.getConfig("roles"));
    }

    /**
     * Возвращает правила маршрутизации для канала {@code channelId}
     * @param channelId идентификатор канала
     * @return парвила маршрутизации для этого канала
     */
    public Set<String> getRouteChannels(String channelId) {
        return routing.getOrDefault(channelId, Collections.emptySet());
    }

    /**
     * Парсит конфигурацию роутинга
     * @param routingConfig конфиг раздела routing
     * @return карта роутинга (ключ - channel_id, значение - список каналов)
     */
    private static Map<String, Set<String>> parseRouting(Config routingConfig) {
        return routingConfig.root().keySet().stream()
                .collect(Collectors.toMap(Function.identity(), key -> Set.copyOf(routingConfig.getStringList(key))));
    }

    /**
     * Парсит конфигурацию ролей
     * @param rolesConfig конфиг раздела roles
     * @return карта конфигураций ролей (ключ - channel_id, значение - конфигурация ролей)
     */
    private static Map<String, ChannelRolesConfiguration> parseRoles(Config rolesConfig) {
        return rolesConfig.root().keySet().stream()
                .collect(Collectors.toMap(Function.identity(), channelId -> new ChannelRolesConfiguration(rolesConfig.getConfig(channelId))));
    }
}