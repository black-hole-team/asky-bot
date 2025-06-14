package team.blackhole.bot.asky.security;

import lombok.Builder;

/**
 * Пользователь сообщения
 * @param id   идентификатор пользователя
 * @param role роль пользователя
 */
@Builder
public record AskyUser(long id, AskyUserRole role) {
}
