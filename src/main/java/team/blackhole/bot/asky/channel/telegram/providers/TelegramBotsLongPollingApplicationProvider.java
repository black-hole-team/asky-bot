package team.blackhole.bot.asky.channel.telegram.providers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Provider;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.longpolling.util.TelegramOkHttpClientFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Поставщик long-pool приложения для канала telegram-бота
 */
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class TelegramBotsLongPollingApplicationProvider implements Provider<TelegramBotsLongPollingApplication> {

    /** Маппер объектов */
    private final ObjectMapper objectMapper;

    @Override
    public TelegramBotsLongPollingApplication get() {
        var threadFactory = new BotThreadFactory();
        return new TelegramBotsLongPollingApplication(
                () -> objectMapper,
                new TelegramOkHttpClientFactory.DefaultOkHttpClientCreator(),
                () -> Executors.newSingleThreadScheduledExecutor(threadFactory));
    }

    /**
     * Поток для обработки бота
     */
    protected static class BotThread extends Thread {

        /**
         * Конструктор
         * @param target задача для обработки
         */
        public BotThread(Runnable target) {
            super(target);
        }

        @Override
        public void run() {
            try {
                super.run();
            } catch (Exception e) {
                log.error("Ошибка в процессе обработки сообщения бота", e);
            }
        }
    }

    /**
     * Фабрика потоков для бота
     */
    protected static class BotThreadFactory implements ThreadFactory {

        @Override
        public Thread newThread(@NotNull Runnable target) {
            return new BotThread(target);
        }
    }
}
