package team.blackhole.bot.asky.channel;

import java.util.Optional;

/**
 * Интерфейс канала обмена сообщениями
 */
public interface Channel {

    /**
     * Запускает бота
     */
    void start();

    /**
     * Останавливает бота
     */
    void stop();

    /**
     * Взвращает идентификатор бота
     * @return идентификатор бота
     */
    String getId();

    /**
     * Возвращает признак запущенного бота
     * @return {@code true}, если бот запущен {@code false}, если иначе
     */
    boolean isAlive();

    /**
     * Возвращает опциональное значение возможности канала
     * @param capabilityClass класс возможности
     * @return возможность канала
     * @param <T> тип возможности
     */
    <T extends ChannelCapability> Optional<T> getCapability(Class<T> capabilityClass);
}
