package team.blackhole.bot.asky.channel.telegram;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;
import org.eclipse.jetty.client.HttpClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import team.blackhole.bot.asky.channel.telegram.providers.HttpClientProvider;
import team.blackhole.bot.asky.channel.telegram.providers.TelegramBotsLongPollingApplicationProvider;

/**
 * Модуль канала телеграм бота
 */
public class TelegramBotChannelModule implements Module {

    /** Параметр канала - токен бота */
    public static final String BOT_TOKEN_CHANNEL_PARAM = "token";

    /** Параметр канала - максимальный размер файла */
    public static final String BOT_MAX_FILE_SIZE_PARAM = "max_file_size";

    @Override
    public void configure(Binder binder) {
        binder.bind(HttpClient.class).toProvider(HttpClientProvider.class).in(Scopes.SINGLETON);
        binder.bind(TelegramBotsLongPollingApplication.class).toProvider(TelegramBotsLongPollingApplicationProvider.class).in(Scopes.SINGLETON);
        binder.bind(TelegramBotChannelFactory.class).in(Scopes.SINGLETON);
    }
}
