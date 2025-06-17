package team.blackhole.bot.asky.service.ticket;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;

/**
 * Модуль сервиса обращений
 */
public class TicketServiceModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(TicketService.class).to(TicketServiceImpl.class).in(Scopes.SINGLETON);
        binder.bind(TicketAfterResolveService.class).to(TicketAfterResolveServiceImpl.class).in(Scopes.SINGLETON);
    }
}
