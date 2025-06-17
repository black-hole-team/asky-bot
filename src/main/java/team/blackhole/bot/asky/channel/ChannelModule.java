package team.blackhole.bot.asky.channel;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;
import team.blackhole.bot.asky.channel.sending.MessageSenderModule;
import team.blackhole.bot.asky.channel.telegram.TelegramBotChannelModule;
import team.blackhole.bot.asky.channel.webhook.WebhookServer;
import team.blackhole.bot.asky.channel.webhook.WebhookServerImpl;

/**
 * Модуль для работы с каналами получения сообщений
 */
public class ChannelModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.install(new TelegramBotChannelModule());
        binder.install(new MessageSenderModule());

        binder.bind(WebhookServer.class).to(WebhookServerImpl.class).in(Scopes.SINGLETON);
        binder.bind(ChannelPool.class).toProvider(ChannelPoolProvider.class).in(Scopes.SINGLETON);
    }
}
