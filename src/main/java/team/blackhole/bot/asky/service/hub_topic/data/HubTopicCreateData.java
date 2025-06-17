package team.blackhole.bot.asky.service.hub_topic.data;

import lombok.Builder;

/**
 * Данные для создания темы хаба
 * @param hubId      идентификатор хаба
 * @param ticketId   идентификатор заявки
 * @param hubTopicId идентификатор темы на стороне хаба
 */
@Builder
public record HubTopicCreateData(long hubId, long ticketId, String hubTopicId) {
}
