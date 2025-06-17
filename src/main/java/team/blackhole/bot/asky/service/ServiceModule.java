package team.blackhole.bot.asky.service;

import com.google.inject.Binder;
import com.google.inject.Module;
import team.blackhole.bot.asky.service.chat.ChatServiceModule;
import team.blackhole.bot.asky.service.hub.HubServiceModule;
import team.blackhole.bot.asky.service.hub_topic.HubTopicServiceModule;
import team.blackhole.bot.asky.service.ticket.TicketServiceModule;

/**
 * Модуль сервисов
 */
public class ServiceModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.install(new ChatServiceModule());
        binder.install(new HubServiceModule());
        binder.install(new HubTopicServiceModule());
        binder.install(new TicketServiceModule());
    }
}
