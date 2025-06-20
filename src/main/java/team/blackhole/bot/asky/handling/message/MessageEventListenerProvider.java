package team.blackhole.bot.asky.handling.message;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Provider;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import team.blackhole.bot.asky.config.AskyHandlingConfiguration;
import team.blackhole.bot.asky.db.jedis.repository.StageRepository;
import team.blackhole.bot.asky.handling.message.command.handlers.NoneHandler;

import java.util.concurrent.Executors;

/**
 * Поставщик обработчика событий новых сообщений
 */
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class MessageEventListenerProvider implements Provider<MessageEventListener> {

    /** Репозитория для работы со стадией */
    private final StageRepository stageRepository;

    /** Конфигурация обработки сообщений */
    private final AskyHandlingConfiguration handlingConfiguration;

    /** Обработчик стадии {@link StageName#NONE} */
    private final NoneHandler noneStageHandler;

    /** Фабрика сессий hibernate */
    private final SessionFactory sessionFactory;

    /** Шина событий */
    private final EventBus eventBus;

    @Override
    public MessageEventListener get() {
        var listenerThreadsCount = handlingConfiguration.getHandlingThreadsCount();
        var listener = new MessageEventListener(stageRepository, Executors.newFixedThreadPool(listenerThreadsCount == -1 ?
                Runtime.getRuntime().availableProcessors() : listenerThreadsCount), sessionFactory);
        listener.register(StageName.NONE, noneStageHandler);
        eventBus.register(listener);
        return listener;
    }
}
