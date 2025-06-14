package team.blackhole.bot.asky.handling;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Provider;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import team.blackhole.bot.asky.config.AskyHandlingConfiguration;
import team.blackhole.bot.asky.handling.stage.StageManager;

import java.util.concurrent.Executors;

/**
 * Поставщик слушателя
 */
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class MessageEventListenerProvider implements Provider<MessageEventListener> {

    /** Менеджер стадий */
    private final StageManager stageManager;

    /** Шина событий */
    private final EventBus eventBus;

    /** Фабрика сессий hibernate */
    private final SessionFactory sessionFactory;

    /** Конфигурация обработки сообщений */
    private final AskyHandlingConfiguration handlingConfiguration;

    @Override
    public MessageEventListener get() {
        var listenerThreadsCount = handlingConfiguration.getHandlingThreadsCount();
        var listener = new MessageEventListener(stageManager, Executors.newFixedThreadPool(listenerThreadsCount == -1 ?
                Runtime.getRuntime().availableProcessors() : listenerThreadsCount), sessionFactory);
        eventBus.register(listener);
        return listener;
    }
}
