package team.blackhole.bot.asky.handling.command.filtering;

import team.blackhole.data.filter.Filter;

/**
 * Помощник для работы с фильтром
 */
public interface FilterHelper {

    /**
     * Возвращает текущий фильтр
     * @return текущий фильтр
     */
    Filter getFilter();

    /**
     * Переустанавливает фильтр
     * @param filterText текст фильтра
     */
    void reset(String filterText);

    /**
     * Устанавливает новую страницу как текущую страницу фильтра
     * @param page новая страница фильтра
     */
    void setPage(int page);

    /**
     * Увеличивает текущую страницу на значение {@code delta}
     * @param delta разница между текущей и новой страницей
     */
    void incrementPage(int delta);

    /**
     * Возвращает префикс списка
     * @return префикс списка
     */
    String getPrefix();
}
