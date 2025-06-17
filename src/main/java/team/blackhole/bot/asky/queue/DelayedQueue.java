package team.blackhole.bot.asky.queue;

import java.time.Duration;
import java.time.temporal.TemporalAccessor;
import java.util.List;

/**
 * Очередь позволяющая откладывать получение из неё элементов
 * @param <T> тип элементов очереди
 */
public interface DelayedQueue<T> {

    /**
     * Добавляет элемент в отложенную очередь
     * @param element элемент для обработки
     * @param delay   задержка получения этого элемента
     */
    void add(T element, Duration delay);

    /**
     * Добавляет элемент в отложенную очередь
     * @param element  элемент для обработки
     * @param temporal время истечения срока действия
     */
    void add(T element, TemporalAccessor temporal);

    /**
     * Извлекает элементы, готовые к обработке
     * @return список элементов для обработки
     */
    List<T> pollExpired();
}