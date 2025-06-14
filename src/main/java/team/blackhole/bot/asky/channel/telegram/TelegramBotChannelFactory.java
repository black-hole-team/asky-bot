package team.blackhole.bot.asky.channel.telegram;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.eclipse.jetty.client.HttpClient;
import org.telegram.telegrambots.client.jetty.JettyTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.TelegramUrl;
import team.blackhole.bot.asky.channel.Channel;
import team.blackhole.bot.asky.channel.ChannelCapability;
import team.blackhole.bot.asky.channel.ChannelFactory;
import team.blackhole.bot.asky.channel.capability.ChatCapability;
import team.blackhole.bot.asky.channel.capability.HubCapability;
import team.blackhole.bot.asky.config.AskyChannelConfiguration;
import team.blackhole.bot.asky.support.FileUtils;

import java.util.HashMap;
import java.util.Objects;

/**
 * Фабрика каналов телеграмм
 */
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class TelegramBotChannelFactory implements ChannelFactory {

    /** Приложение выполняющее long-pool запросы к telegram api */
    private final TelegramBotsLongPollingApplication telegramBotsLongPollingApplication;

    /** Маппер объектов */
    private final ObjectMapper objectMapper;

    /** HTTP клиент */
    private final HttpClient httpClient;

    /** Шина событий */
    private final EventBus eventBus;

    @Override
    public Channel create(AskyChannelConfiguration configuration) {
        var token = Objects.requireNonNull(configuration.getParam(TelegramBotChannelModule.BOT_TOKEN_CHANNEL_PARAM));
        var client = new JettyTelegramClient(objectMapper, httpClient, token, TelegramUrl.DEFAULT_URL);
        var updateHandler = new TelegramBotChannelUpdateHandler(client, eventBus, configuration.getId(), token);
        var capabilities = new HashMap<Class<? extends ChannelCapability>, ChannelCapability>();
        capabilities.put(ChatCapability.class, new TelegramBotChatCapability(client, FileUtils
                .parseFileSize(configuration.getParam(TelegramBotChannelModule.BOT_MAX_FILE_SIZE_PARAM, "50MB"))));
        capabilities.put(HubCapability.class, new TelegramBotHubCapability(client));
        return new TelegramBotChannel(configuration, client, objectMapper, updateHandler, telegramBotsLongPollingApplication, capabilities);
    }
}
