package team.blackhole.bot.asky.channel;

import team.blackhole.bot.asky.support.exception.AskyException;

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
     * Возвращает идентификатор бота
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

    /**
     * Возвращает значение возможности канала или выбрасывает исключение, если её нет
     * @param capabilityClass класс возможности
     * @return возможность канала
     * @param <T> тип возможности
     */
    default <T extends ChannelCapability> T getCapabilityOrThrow(Class<T> capabilityClass) {
        return getCapability(capabilityClass).orElseThrow(() -> new AskyException("У текущего канала '%s' нет возможности '%s'"
                .formatted(getClass().getSimpleName(), capabilityClass.getSimpleName())));
    }
}
