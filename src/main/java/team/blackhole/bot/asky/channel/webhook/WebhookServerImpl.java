package team.blackhole.bot.asky.channel.webhook;

import com.google.inject.Inject;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import team.blackhole.bot.asky.channel.ChannelPool;
import team.blackhole.bot.asky.config.AskyWebhookConfiguration;
import team.blackhole.bot.asky.support.exception.AskyException;

/**
 * Сервер для обработки вебхуков
 */
@Log4j2
public class WebhookServerImpl implements WebhookServer {

    /** Конфигурация сервера обработки вебхуков */
    private final AskyWebhookConfiguration webhookConfiguration;

    /** Пул ботов */
    private final ChannelPool channelPool;

    /** Приложение */
    private Javalin app;

    /**
     * Конструктор
     * @param webhookConfiguration конфигурация сервера обработки вебхуков
     * @param channelPool              пул ботов
     */
    @Inject
    public WebhookServerImpl(AskyWebhookConfiguration webhookConfiguration, ChannelPool channelPool) {
        this.webhookConfiguration = webhookConfiguration;
        this.channelPool = channelPool;
    }

    @Override
    public void start() {
        if (app != null) {
            throw new AskyException("Webhook сервер уже запущен");
        }
        app = Javalin.create()
                .post(webhookConfiguration.getBase(), new BotPoolWebhookServerHandler())
                .start(webhookConfiguration.getPort());
    }

    @Override
    public void close() {
        if (app == null) {
            throw new AskyException("Webhook сервер не был запущен ранее");
        }
        app.stop();
        app = null;
    }

    /**
     * Обработчик вебхуков
     */
    private final class BotPoolWebhookServerHandler implements Handler {

        @Override
        public void handle(@NotNull Context ctx) {
            var name = ctx.pathParam("name");
            var channel = channelPool.getChannelById(name);

            try {
                if (channel instanceof WebhookHandler webhookHandler) {
                    if (channel.isAlive()) {
                        webhookHandler.handle(ctx.bodyAsBytes());
                    }
                }
            } catch (Exception e) {
                log.info("Ошибка при обработке вебхука для бота {}", name, e);
            }
        }
    }
}
