package team.blackhole.bot.asky.channel.telegram;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.forum.CreateForumTopic;
import org.telegram.telegrambots.meta.api.methods.forum.DeleteForumTopic;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import team.blackhole.bot.asky.channel.capability.HubCapability;
import team.blackhole.bot.asky.support.exception.AskyException;

/**
 * Возможность управления хабами telegram бота
 */
@RequiredArgsConstructor
public class TelegramBotHubCapability implements HubCapability {

    /** Клиент telegram */
    private final TelegramClient client;

    @Override
    public HubInfo getInfo(String hubId) {
        try {
            var chat = client.execute(new GetChat(hubId));
            return new HubInfo(chat.getTitle());
        } catch (TelegramApiException e) {
            throw new AskyException("Ошибка получения информации о хабе [hubId = %s]".formatted(hubId), e);
        }
    }

    @Override
    public HubTopicInfo createHubTopic(String hubId, String name) {
        try {
            var topic = client.execute(new CreateForumTopic(hubId, name));
            return new HubTopicInfo(String.valueOf(topic.getMessageThreadId()), hubId, topic.getName());
        } catch (TelegramApiException e) {
            throw new AskyException("Ошибка создания темы хаба [hubId = %s]".formatted(hubId), e);
        }
    }

    @Override
    public void deleteHubTopic(String hubId, String topicId) {
        try {
            if (!client.execute(new DeleteForumTopic(hubId, Integer.parseInt(topicId)))) {
                throw new AskyException("Не удалось удалить тему хаба [hubId = %s, topicId = %s]".formatted(hubId, topicId));
            }
        } catch (TelegramApiException e) {
            throw new AskyException("Ошибка удаления темы хаба [hubId = %s, topicId = %s]".formatted(hubId, topicId), e);
        }
    }
}
