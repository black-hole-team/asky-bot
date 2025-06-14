package team.blackhole.bot.asky.channel.webhook;

/**
 * Обработчик данных полученных из вебхука
 */
public interface WebhookHandler {

    /**
     * Обрабатывает данные вебхука
     * @param webhook данные вебхука
     */
    void handle(byte[] webhook);
}
