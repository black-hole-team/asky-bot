package team.blackhole.bot.asky.scheduling;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;
import team.blackhole.bot.asky.scheduling.jobs.TicketScheduling;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Сервис выполнения периодических задач
 */
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class SchedulingService implements Runnable, AutoCloseable {

    /** Исполнитель периодических задач */
    private final ScheduledExecutorService threadPoolExecutor;

    /** Периодическая задача заявок */
    private final TicketScheduling ticketScheduling;

    @Override
    public void run() {
        threadPoolExecutor.scheduleWithFixedDelay(ticketScheduling, 0, 10, TimeUnit.SECONDS);
    }

    @Override
    public void close() {
        threadPoolExecutor.shutdownNow();
    }
}
