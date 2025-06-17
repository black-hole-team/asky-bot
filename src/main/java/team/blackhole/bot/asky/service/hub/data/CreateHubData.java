package team.blackhole.bot.asky.service.hub.data;

import lombok.Builder;

/**
 * Данные для создания хаба
 * @param name         наименование хаба
 * @param channelId    идентификатор канала
 * @param channelHubId идентификатор хаба в канале
 */
@Builder
public record CreateHubData(String name, String channelId, String channelHubId) {
}
