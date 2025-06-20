package team.blackhole.bot.asky.service.hub.data;

import lombok.Builder;
import team.blackhole.bot.asky.db.hibernate.domains.HubType;

/**
 * Данные для создания хаба
 * @param name         наименование хаба
 * @param type         тип хаба
 * @param channelId    идентификатор канала
 * @param channelHubId идентификатор хаба в канале
 */
@Builder
public record CreateHubData(String name, HubType type, String channelId, String channelHubId) {
}
