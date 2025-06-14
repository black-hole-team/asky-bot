package team.blackhole.bot.asky.channel;

import java.util.NoSuchElementException;

/**
 * Пул каналов
 */
public interface ChannelPool extends AutoCloseable {

    /**
     * Возвращает канал по его наименованию
     * @param id идентификатор канала
     * @return канал по наименованию
     * @throws NoSuchElementException если канал не найден
     */
    Channel getChannelById(String id) throws NoSuchElementException;

    /**
     * Запускает пул каналов
     */
    void start();
}
