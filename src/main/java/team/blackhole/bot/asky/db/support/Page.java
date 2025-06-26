package team.blackhole.bot.asky.db.support;

import team.blackhole.data.filter.Filter;
import lombok.NonNull;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Страница данных
 * @param <T> страница данных
 */
public interface Page<T> extends Iterable<T> {

    /**
     * Возвращает содержимое страницы
     * @return содержимое страницы
     */
    List<T> getContent();

    /**
     * Возвращает номер текущей страницы
     * @return номер текущей страницы
     */
    int getPageNumber();

    /**
     * Возвращает общее количество элементов
     * @return общее количество элементов
     */
    long getTotalElements();

    /**
     * Возвращает фильтр, по которому страницы была получена
     * @return фильтр, по которому страницы была получена
     */
    Filter getFilter();

    /**
     * Возвращает размер страницы
     * @return размер страницы
     */
    default int size() {
        return getContent().size();
    }

    /**
     * Возвращает итератор содержимого страницы
     * @return итератор содержимого страницы
     */
    @NonNull
    @Override
    default Iterator<T> iterator() {
        return getContent().iterator();
    }

    /**
     * Возвращает поток с содержимым страницы
     * @return поток с содержимым страницы
     */
    default Stream<T> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    /**
     * Возвращает поток с содержимым страницы
     * @return поток с содержимым страницы
     */
    default Stream<T> parallelStream() {
        return StreamSupport.stream(spliterator(), true);
    }

    /**
     * Возвращает признак пустой страницы
     * @return признак пустой страницы
     */
    default boolean isEmpty() {
        return getContent().isEmpty();
    }

    /**
     * Возвращает признак пустой страницы
     * @return признак пустой страницы
     */
    default boolean hasNext() {
        return (long) getFilter().getPageSize() * (getPageNumber() + 1) < getTotalElements();
    }

    /**
     * Возвращает признак возможности запроса
     * @return признак пустой страницы
     */
    default boolean hasPrev() {
        return getPageNumber() > 0;
    }
}