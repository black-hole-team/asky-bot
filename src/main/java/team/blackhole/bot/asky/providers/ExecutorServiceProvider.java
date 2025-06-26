package team.blackhole.bot.asky.providers;

import com.google.inject.Inject;
import com.google.inject.Provider;
import lombok.RequiredArgsConstructor;
import team.blackhole.bot.asky.config.AskyHandlingConfiguration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Поставщик сервиса исполнителя
 */
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class ExecutorServiceProvider implements Provider<ExecutorService> {

    /** Конфигурация обработки сообщений */
    private final AskyHandlingConfiguration handlingConfiguration;

    @Override
    public ExecutorService get() {
        var listenerThreadsCount = handlingConfiguration.getHandlingThreadsCount();
        return Executors.newFixedThreadPool(listenerThreadsCount == -1 ? Runtime.getRuntime().availableProcessors() : listenerThreadsCount);
    }
}
