package team.blackhole.bot.asky.channel;

import com.google.inject.Inject;
import com.google.inject.Provider;
import lombok.RequiredArgsConstructor;
import team.blackhole.bot.asky.channel.telegram.TelegramBotChannelFactory;
import team.blackhole.bot.asky.config.AskyChannelConfiguration;
import team.blackhole.bot.asky.config.AskyChannelsConfiguration;

import java.util.HashMap;

/**
 * Поставщик пула каналов
 */
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class ChannelPoolProvider implements Provider<ChannelPool> {

    /** Конфигурация пула каналов */
    private final AskyChannelsConfiguration botsConfiguration;

    /** Фабрика каналов типа телеграмм бота */
    private final TelegramBotChannelFactory telegramBotFactory;

    @Override
    public ChannelPool get() {
        var bots = new HashMap<String, Channel>();
        for (var bot : botsConfiguration.getChannels().entrySet()) {
            bots.put(bot.getKey(), create(bot.getValue()));
        }
        return new ChannelPoolImpl(bots);
    }

    /**
     * Создает канал по его конфигурации
     * @param configuration конфигурация канала
     * @return созданный канал
     */
    private Channel create(AskyChannelConfiguration configuration) {
        return telegramBotFactory.create(configuration);
    }
}
