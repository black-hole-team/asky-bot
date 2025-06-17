package team.blackhole.bot.asky.queue;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;
import team.blackhole.bot.asky.queue.impl.TicketDelayedQueue;

/**
 * Модуль очередей
 */
public class QueueModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(TicketDelayedQueue.class).in(Scopes.SINGLETON);
    }
}
