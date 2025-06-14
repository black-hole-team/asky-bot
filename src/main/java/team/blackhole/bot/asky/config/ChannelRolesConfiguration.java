package team.blackhole.bot.asky.config;

import com.typesafe.config.Config;
import lombok.Getter;
import team.blackhole.bot.asky.security.AskyUserRole;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Конфигурация ролей для канала
 */
@Getter
public class ChannelRolesConfiguration {

    /** Карта ролей пользователей (ключ - ID пользователя или "*", значение - роль) */
    private final Map<String, AskyUserRole> userRoles;

    /**
     * Конструктор
     * @param config свойства
     */
    public ChannelRolesConfiguration(Config config) {
        var roles = new HashMap<String, AskyUserRole>();
        for (var entry : config.entrySet()) {
            var userId = entry.getKey();
            var roleName = config.getString(userId);
            roles.put("\"*\"".equals(userId) ? "*" : userId, AskyUserRole.valueOf(roleName));
        }
        userRoles = Collections.unmodifiableMap(roles);
    }

    /**
     * Возвращает роль пользователя для канала
     * @param userId ID пользователя
     * @return роль пользователя (если не найдено - возвращает дефолтную роль "*")
     */
    public AskyUserRole getRole(long userId) {
        var stringUserId = String.valueOf(userId);
        // Сначала проверяем конкретную роль пользователя
        if (userRoles.containsKey(stringUserId)) {
            return userRoles.get(stringUserId);
        }
        // Возвращаем дефолтную роль
        return userRoles.get("*");
    }
}