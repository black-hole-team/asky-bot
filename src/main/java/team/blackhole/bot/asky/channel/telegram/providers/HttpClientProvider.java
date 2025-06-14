package team.blackhole.bot.asky.channel.telegram.providers;

import com.google.inject.Provider;
import org.eclipse.jetty.client.HttpClient;
import team.blackhole.bot.asky.support.exception.AskyException;

/**
 * Поставщик HTTP клиента
 */
public class HttpClientProvider implements Provider<HttpClient> {

    @Override
    public HttpClient get() {
        try {
            var client = new HttpClient();
            client.start();
            return client;
        } catch (Exception e) {
            throw new AskyException("Ошибка при создании HTTP клиента", e);
        }
    }
}
