package team.blackhole.bot.asky.handling.command.filtering;

import lombok.RequiredArgsConstructor;
import team.blackhole.bot.asky.channel.capability.ChatCapability;
import team.blackhole.bot.asky.channel.sending.renderer.MessageRenderer;
import team.blackhole.bot.asky.db.support.Page;
import team.blackhole.bot.asky.support.MessageSource;
import team.blackhole.data.filter.Filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;

/**
 * Отрисовщик страницы
 */
@RequiredArgsConstructor
public abstract class PageRenderer<T> implements MessageRenderer {

    /** Шаблон полного действия перехода к странице */
    public static final String PAGE_ACTION_TEMPLATE = "page:%s:%s";

    /** Действие перехода к следующей странице */
    public static final String NEXT_ACTION = "next";

    /** Действие перехода к предыдущей странице */
    public static final String PREV_ACTION = "prev";

    /** Фильтр */
    private final Filter filter;

    /** Страница полученная по этому фильтру */
    private final Page<T> page;

    /** Источник сообщений */
    private final MessageSource messageSource;

    /**
     * Возвращает префикс списка
     * @return префикс списка
     */
    protected abstract String getPrefix();

    /**
     * Возвращает аргументы форматирования строки
     * @param entity сущность
     * @return аргументы форматирования строки
     */
    protected abstract Object[] getRowFormat(T entity);

    /**
     * Возвращает общее количество страниц
     * @return общее количество страниц
     */
    private long getTotalPages() {
        if (page.getTotalElements() == 0 || filter.getPageSize() == 0) {
            return 0;
        }
        return (long) Math.ceil((double) page.getTotalElements() / filter.getPageSize());
    }

    @Override
    public String render(Locale locale) {
        var builder = new StringJoiner("\n\n");
        for (var entity : page) {
            builder.add(messageSource.getMessage("list$" + getPrefix() + "_item", locale, getRowFormat(entity)));
        }
        return messageSource.getMessage("list$" + getPrefix(), locale,
                Math.min(page.size(), page.getTotalElements()),
                page.getTotalElements(),
                builder.toString(),
                page.getPageNumber() + 1,
                getTotalPages());
    }

    @Override
    public List<List<ChatCapability.MessageAction>> actions(Locale locale) {
        var buttons = new ArrayList<ChatCapability.MessageAction>();
        if (page.getPageNumber() > 0) {
            buttons.add(new ChatCapability.MessageAction(messageSource.getMessage("action$prev_page", locale), PAGE_ACTION_TEMPLATE.formatted(getPrefix(), PREV_ACTION)));
        }
        if (getTotalPages() > page.getPageNumber() + 1) {
            buttons.add(new ChatCapability.MessageAction(messageSource.getMessage("action$next_page", locale), PAGE_ACTION_TEMPLATE.formatted(getPrefix(), NEXT_ACTION)));
        }
        return List.of(buttons);
    }
}