package team.blackhole.bot.asky.channel.webhook;

import com.google.inject.Inject;
import io.jooby.*;
import io.jooby.internal.ForwardingExecutor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import team.blackhole.bot.asky.channel.ChannelPool;
import team.blackhole.bot.asky.config.AskyWebhookConfiguration;
import team.blackhole.bot.asky.config.AskyWebhookSSLConfiguration;
import team.blackhole.bot.asky.support.exception.AskyException;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.concurrent.Executors;

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
    private Server server;

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
        if (server != null) {
            throw new AskyException("Webhook сервер уже запущен");
        }
        var options = getOptions();
        server = Server.loadServer(options).start(createJooby());
        log.info("Webhook сервер запущен на порту {}", options.getPort());
    }

    @Override
    public void close() {
        if (server == null) {
            throw new AskyException("Webhook сервер не был запущен ранее");
        }
        server.stop();
        server = null;
        log.info("Webhook сервер остановлен");
    }

    /**
     * Создает и возвращает jooby приложение
     * @return jooby приложение
     */
    @NotNull
    private Jooby createJooby() {
        var jooby = new Jooby();
        jooby.setStartupSummary(Collections.emptyList());
        jooby.setWorker(new ForwardingExecutor());
        jooby.setDefaultWorker(Executors.newSingleThreadExecutor());
        jooby.post(webhookConfiguration.getBase(), new BotPoolWebhookServerHandler());
        return jooby;
    }

    /**
     * Возвращает опции запуска webhook сервера
     * @return опции запуска webhook сервера
     */
    @NotNull
    private ServerOptions getOptions() {
        var options = new ServerOptions();
        options.setPort(webhookConfiguration.getPort());
        options.setHost(webhookConfiguration.getHost());
        if (webhookConfiguration.getSsl() != null) {
            var ssl = webhookConfiguration.getSsl();
            options.setSsl(getSslOptions(ssl));
            options.setSecurePort(ssl.getPort());
        }
        return options;
    }

    /**
     * Возвращает ssl опции
     * @param sslConfiguration ssl конфигурация
     * @return ssl опции
     */
    private SslOptions getSslOptions(AskyWebhookSSLConfiguration sslConfiguration) {
        try {
            var result = new SslOptions();
            var type = sslConfiguration.getType();
            switch (type) {
                case "X.509" -> {
                    result.setCert(Files.newInputStream(sslConfiguration.getCertPath()));
                    result.setPrivateKey(Files.newInputStream(sslConfiguration.getKeyPath()));
                    result.setPassword(sslConfiguration.getKeyPassword());
                }
                case "PKCS12" -> {
                    result.setCert(Files.newInputStream(sslConfiguration.getCertPath()));
                    result.setPassword(sslConfiguration.getCertPassword());
                    if (sslConfiguration.getTrustCertPath() != null) {
                        result.setTrustCert(Files.newInputStream(sslConfiguration.getTrustCertPath()));
                        result.setTrustPassword(sslConfiguration.getTrustCertPassword());
                    }
                }
                default -> throw new IllegalArgumentException("Неожиданный тип SSL конфигурации '%s'".formatted(type));
            }
            result.setProtocol(sslConfiguration.getProtocols());
            result.setType(type);
            return result;
        } catch (IOException e) {
            throw new AskyException("Ошибка при попытки загрузить ssl опции", e);
        }
    }

    /**
     * Обработчик вебхуков
     */
    private final class BotPoolWebhookServerHandler implements Route.Handler {

        @NotNull
        @Override
        public Object apply(@NotNull Context ctx) {
            var name = ctx.path("name").to(String.class);
            var channel = channelPool.getChannelById(name);
            try {
                if (channel instanceof WebhookHandler webhookHandler) {
                    if (channel.isAlive()) {
                        try (var stream = ctx.body().stream()) {
                            webhookHandler.handle(stream.readAllBytes());
                        }
                        return StatusCode.OK;
                    }
                }
            } catch (Exception e) {
                log.info("Ошибка при обработке вебхука для бота {}", name, e);
            }
            return StatusCode.SERVER_ERROR;
        }
    }
}
