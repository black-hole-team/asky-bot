package team.blackhole.bot.asky.service.chat;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;
import team.blackhole.bot.asky.db.hibernate.domains.Chat;
import team.blackhole.bot.asky.db.hibernate.repository.ChatRepository;
import team.blackhole.bot.asky.service.chat.data.CreateChatData;

import java.util.Optional;

/**
 * Реализация сервиса для работы с чатами {@link ChatService}
 */
@RequiredArgsConstructor(onConstructor_ = @__(@Inject))
public class ChatServiceImpl implements ChatService {

    /** Репозиторий для работы с чатами */
    private final ChatRepository chatRepository;

    @Override
    public Chat create(CreateChatData data) {
        var chat = new Chat();
        chat.setChannelChatId(data.channelChatId());
        chat.setChannelId(data.channelId());
        return chatRepository.save(chat);
    }

    @Override
    public Optional<Chat> findChatByChannelChatIdAndChannelId(String channelId, String channelChatId) {
        return chatRepository.findChatByChannelChatIdAndChannelId(channelId, channelChatId);
    }
}
