package team.blackhole.bot.asky.channel;

import team.blackhole.bot.asky.config.AskyChannelConfiguration;

/**
 * Фабрика каналов
 */
public interface ChannelFactory {

    /**
     * Создает канал на основе конфигурации
     * @param configuration конфигурация канала
     * @return канал
     */
    Channel create(AskyChannelConfiguration configuration);
}
