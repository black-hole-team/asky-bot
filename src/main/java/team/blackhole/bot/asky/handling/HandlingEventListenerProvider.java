package team.blackhole.bot.asky.handling;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Provider;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import team.blackhole.bot.asky.channel.ChannelCallback;
import team.blackhole.bot.asky.channel.ChannelMessage;
import team.blackhole.bot.asky.db.jedis.repository.StageRepository;
import team.blackhole.bot.asky.handling.command.callback.handlers.NoneCallbackHandler;
import team.blackhole.bot.asky.handling.command.message.handlers.NoneMessageHandler;

import java.util.concurrent.ExecutorService;

/**
 * Поставщик обработчика событий новых сообщений
 */
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class HandlingEventListenerProvider implements Provider<HandlingEventListener> {

    /** Репозитория для работы со стадией */
    private final StageRepository stageRepository;

    /** Обработчик сообщений на стадии {@link StageName#NONE} */
    private final NoneMessageHandler noneMessageHandler;

    /** Обработчик данных обратного вызова на стадии {@link StageName#NONE} */
    private final NoneCallbackHandler noneCallbackHandler;

    /** Фабрика сессий hibernate */
    private final SessionFactory sessionFactory;

    /** Сервис исполнитель */
    private final ExecutorService executorService;

    /** Шина событий */
    private final EventBus eventBus;

    @Override
    public HandlingEventListener get() {
        var listener = new HandlingEventListener(stageRepository, executorService, sessionFactory);
        listener.register(StageName.NONE, ChannelMessage.class, noneMessageHandler);
        listener.register(StageName.NONE, ChannelCallback.class, noneCallbackHandler);
        eventBus.register(listener);
        return listener;
    }
}
