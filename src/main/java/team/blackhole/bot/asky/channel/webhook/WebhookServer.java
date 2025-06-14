package team.blackhole.bot.asky.channel.webhook;

/**
 * Сервер для обработки вебхуков
 */
public interface WebhookServer extends AutoCloseable {

    /**
     * Запускает сервер
     */
    void start();
}
