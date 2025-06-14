package team.blackhole.bot.asky.service.hub.data;

import lombok.Builder;

/**
 * Данные для создания хаба
 * @param id        идентификатор хаба
 * @param name      наименование хаба
 * @param channelId идентификатор канала
 */
@Builder
public record CreateHubData(long id, String name, String channelId) {
}
