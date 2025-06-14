package team.blackhole.bot.asky.service.chat;

import com.google.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import team.blackhole.bot.asky.db.hibernate.domains.Chat;
import team.blackhole.bot.asky.db.hibernate.domains.ChatId;
import team.blackhole.bot.asky.db.hibernate.repository.ChatRepository;

/**
 * Реализация сервиса для работы с чатами {@link ChatService}
 */
@RequiredArgsConstructor(onConstructor_ = @__(@Inject))
public class ChatServiceImpl implements ChatService {

    /** Репозиторий для работы с чатами */
    private final ChatRepository chatRepository;

    @Override
    @Transactional
    public Chat findByIdOrCreate(ChatId chatId) {
        return chatRepository.findById(chatId).orElseGet(() -> {
            var chat = new Chat();
            chat.setId(chatId);
            return chatRepository.save(chat);
        });
    }
}
