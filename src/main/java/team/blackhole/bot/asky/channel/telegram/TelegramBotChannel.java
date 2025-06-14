package team.blackhole.bot.asky.channel.telegram;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.updates.DeleteWebhook;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import team.blackhole.bot.asky.channel.Channel;
import team.blackhole.bot.asky.channel.ChannelCapability;
import team.blackhole.bot.asky.channel.webhook.WebhookHandler;
import team.blackhole.bot.asky.config.AskyChannelConfiguration;
import team.blackhole.bot.asky.support.exception.AskyException;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * Канал телеграмм бота
 */
@Log4j2
@RequiredArgsConstructor
public class TelegramBotChannel implements Channel, WebhookHandler {

    /** Конфигурация бота */
    private final AskyChannelConfiguration configuration;

    /** Клиент telegram */
    private final TelegramClient telegramClient;

    /** Маппер объектов */
    private final ObjectMapper objectMapper;

    /** Обработчик обновлений */
    private final LongPollingSingleThreadUpdateConsumer updateHandler;

    /** Приложение выполняющее long-pool запросы к telegram api */
    private final TelegramBotsLongPollingApplication telegramBotsLongPollingApplication;

    /** Возможности */
    private final Map<Class<? extends ChannelCapability>, ChannelCapability> capabilities;

    /** Признак активности бота */
    private boolean alive;

    @Override
    public void start() {
        alive = true;
        try {
            if (configuration.isUseWebhook()) {
                telegramClient.execute(new SetWebhook(configuration.getWebhookUrl()));
            } else {
                telegramBotsLongPollingApplication.registerBot(configuration.getParam(TelegramBotChannelModule.BOT_TOKEN_CHANNEL_PARAM), updateHandler);
            }
        } catch (TelegramApiException e) {
            alive = false;
            throw new AskyException("Ошибка при регистрации бота в long-pool приложении", e);
        }
    }

    @Override
    public void stop() {
        alive = false;
        try {
            if (configuration.isUseWebhook()) {
                telegramBotsLongPollingApplication.unregisterBot(configuration.getParam(TelegramBotChannelModule.BOT_TOKEN_CHANNEL_PARAM));
            } else {
                telegramClient.execute(new DeleteWebhook());
            }
        } catch (TelegramApiException e) {
            alive = true;
            throw new AskyException("Ошибка при дерегистрации бота в long-pool приложении", e);
        }
    }

    @Override
    public boolean isAlive() {
        return alive;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends ChannelCapability> Optional<T> getCapability(Class<T> capabilityClass) {
        return Optional.ofNullable((T) capabilities.get(capabilityClass));
    }

    @Override
    public String getId() {
        return configuration.getId();
    }

    @Override
    public void handle(byte[] webhook) {
        try {
            updateHandler.consume(objectMapper.readValue(webhook, Update.class));
        } catch (IOException e) {
            log.error("Ошибка при чтении данных полученных из вебхука");
        }
    }
}
