package team.blackhole.bot.asky.service;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;
import team.blackhole.bot.asky.service.chat.ChatService;
import team.blackhole.bot.asky.service.chat.ChatServiceImpl;
import team.blackhole.bot.asky.service.hub.HubService;
import team.blackhole.bot.asky.service.hub.HubServiceImpl;
import team.blackhole.bot.asky.service.ticket.TicketService;
import team.blackhole.bot.asky.service.ticket.TicketServiceImpl;

/**
 * Модуль сервисов
 */
public class ServiceModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(ChatService.class).to(ChatServiceImpl.class).in(Scopes.SINGLETON);
        binder.bind(HubService.class).to(HubServiceImpl.class).in(Scopes.SINGLETON);
        binder.bind(TicketService.class).to(TicketServiceImpl.class).in(Scopes.SINGLETON);
    }
}
