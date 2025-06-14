package team.blackhole.bot.asky.service.chat.data;

/**
 * Данные для создания чата
 * @param id        идентификатор чата
 * @param channelId идентификатор канала
 */
public record CreateChatData(long id, String channelId) {
}
