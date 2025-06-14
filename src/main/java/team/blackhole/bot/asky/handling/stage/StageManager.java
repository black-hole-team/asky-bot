package team.blackhole.bot.asky.handling.stage;

import lombok.RequiredArgsConstructor;
import team.blackhole.bot.asky.channel.ChannelMessage;
import team.blackhole.bot.asky.channel.ChannelType;
import team.blackhole.bot.asky.config.AskyHandlingConfiguration;
import team.blackhole.bot.asky.db.jedis.domain.Stage;
import team.blackhole.bot.asky.db.jedis.repository.StageRepository;
import team.blackhole.bot.asky.handling.events.MessageEvent;
import team.blackhole.bot.asky.security.AskyUser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Менеджер состояния
 */
@RequiredArgsConstructor
public class StageManager {

    /** Отсутствующая стадия */
    private static final Stage NONE = new Stage(StageName.NONE, Collections.emptyMap(), false);

    /** Карта, где ключ, это наименование стадии, а значение это сама стадия */
    private final Map<StageName, StageHandler> stages = new HashMap<>();

    /** Репозитория для работы со стадией */
    private final StageRepository stageRepository;

    /** Конфигурация обработки сообщений */
    private final AskyHandlingConfiguration handlingConfiguration;

    /**
     * Регистрирует новый обработчик стадии
     * @param name    наименование стадии
     * @param handler обработчик стадии
     */
    public void register(StageName name, StageHandler handler) {
        stages.put(name, handler);
    }

    /**
     * Обрабатывает поступление нового сообщения
     * @param event событие сообщения
     */
    public void process(MessageEvent event) {
        var message = event.getMessage();
        var stage = getStage(message.channelType(), message.userId(), message.chatId());
        var user = getUser(message);
        var firstStageName = stage.name();
        var prevStageName = firstStageName;
        do {
            var handler = stages.get(stage.name());
            if (handler == null) {
                continue;
            }
            prevStageName = stage.name();
            stage = handler.handle(user, stage, event);
        } while (stage.propagation() && prevStageName != stage.name());
        if (stage.name() != firstStageName) {
            updateStage(message.channelType(), message.userId(), message.chatId(), stage);
        }
    }

    /**
     * Возвращает пользователя отправителя сообщения
     * @param message сообщение
     * @return пользователь отправитель сообщения
     */
    private AskyUser getUser(ChannelMessage message) {
        var userRole = handlingConfiguration.getRoles().get(message.channelId()).getRole(message.userId());
        return new AskyUser(message.userId(), userRole);
    }

    /**
     * Возвращает стадию пользователя в чате
     * @param channelType тип канала
     * @param userId      идентификатор пользователя
     * @param chatId      идентификатор чата
     * @return стадия пользователя
     */
    private Stage getStage(ChannelType channelType, long userId, long chatId) {
        return stageRepository.findByKey(getKey(channelType, userId, chatId))
                .orElse(NONE);
    }

    /**
     * Сохраняет стадию пользователя в чате
     * @param channelType тип канала
     * @param userId      идентификатор пользователя
     * @param chatId      идентификатор чата
     * @param stage       стадия для обновления
     */
    private void updateStage(ChannelType channelType, long userId, long chatId, Stage stage) {
        stageRepository.save(getKey(channelType, userId, chatId), stage);
    }

    /**
     * Возвращает ключ стадии в регистре redis
     * @param channelType тип канала
     * @param userId      идентификатор пользователя
     * @param chatId      идентификатор чата
     * @return ключ в регистре redis
     */
    private String getKey(ChannelType channelType, long userId, long chatId) {
        return "%s.%s.%s".formatted(channelType.ordinal(), userId, chatId);
    }
}
