package team.blackhole.bot.asky.scheduling.jobs;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import team.blackhole.bot.asky.service.ticket.TicketAfterResolveService;

/**
 * Класс с задачами выполняемыми по расписанию для обращений
 */
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class TicketScheduling implements Runnable {

    /** Сервис для работы с обращениями после их разрешения */
    private final TicketAfterResolveService ticketAfterResolveService;

    @Override
    public void run() {
        ticketAfterResolveService.doDeleteTopics();
    }
}
