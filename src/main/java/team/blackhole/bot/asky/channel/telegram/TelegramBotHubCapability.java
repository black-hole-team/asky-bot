package team.blackhole.bot.asky.channel.telegram;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.forum.CreateForumTopic;
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
    public HubInfo getInfo(long hubId) {
        try {
            var chat = client.execute(new GetChat(String.valueOf(hubId)));
            return new HubInfo(chat.getTitle());
        } catch (TelegramApiException e) {
            throw new AskyException("Ошибка получения информации о хабе [hubId = %s]".formatted(hubId), e);
        }
    }

    @Override
    public HubTopicInfo createHubTopic(long hubId, String name) {
        try {
            var topic = client.execute(new CreateForumTopic(String.valueOf(hubId), name));
            return new HubTopicInfo(topic.getMessageThreadId(), hubId, topic.getName());
        } catch (TelegramApiException e) {
            throw new AskyException("Ошибка получения информации о хабе [hubId = %s]".formatted(hubId), e);
        }
    }
}
