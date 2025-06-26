package team.blackhole.bot.asky.handling.command.filtering;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import team.blackhole.bot.asky.db.jedis.domain.Stage;
import team.blackhole.data.filter.Filter;
import team.blackhole.data.filter.FilterFacade;
import team.blackhole.data.filter.FilterImpl;
import team.blackhole.data.filter.builder.FilterBuilder;
import team.blackhole.data.filter.builder.FilterBuilderFactory;
import team.blackhole.data.filter.builder.FilterBuilderImpl;

import java.io.StringReader;
import java.util.Collections;

/**
 * Помощник для работы с фильтром
 */
@RequiredArgsConstructor
public class FilterHelperImpl implements FilterHelper {

    /** Пустой фильтр */
    protected static final Filter EMPTY_FILTER = new FilterImpl(0, 20, Collections.emptyList(), Collections.emptyList());

    /** Шаблон ключа текущей страницы */
    private static final String CURRENT_FILTER_TEMPLATE = "list:%s:filter";

    /** Префикс текущей страницы */
    @Getter
    private final String prefix;

    /** Стадия обработки */
    private final Stage stage;

    /** Фасад сервиса фильтрации */
    private final FilterFacade filterFacade;

    /** Фильтр */
    private Filter filter;

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = stage.getData(CURRENT_FILTER_TEMPLATE.formatted(prefix))
                    .map(filter -> filterFacade.read(new StringReader(filter)))
                    .orElse(EMPTY_FILTER);
        }
        return filter;
    }

    @Override
    public void reset(String filterText) {
        saveFilter(filterFacade.read(new StringReader(filterText)));
    }

    @Override
    public void setPage(int page) {
        saveFilter(new ModifingFilterBuilderFactory(getFilter()).create().page(Math.max(page, 0)).build());
    }

    @Override
    public void incrementPage(int delta) {
        saveFilter(new ModifingFilterBuilderFactory(getFilter()).create().incrementPage(0).build());
    }

    /**
     * Сохраняет фильтр в состоянии чата
     * @param filterToSave фильтр для сохранения
     */
    private void saveFilter(Filter filterToSave) {
        stage.setData(FilterHelperImpl.CURRENT_FILTER_TEMPLATE.formatted(prefix), filterFacade.write(filterToSave));
        filter = filterToSave;
    }

    /**
     * Фабрика построителя фильтров для модификации уже имеющегося фильтра
     */
    @RequiredArgsConstructor
    private static class ModifingFilterBuilderFactory implements FilterBuilderFactory {

        /** Фильтр */
        private final Filter filter;

        @Override
        public FilterBuilder create() {
            return new FilterBuilderImpl()
                    .page(filter.getPage())
                    .pageSize(filter.getPageSize())
                    .criteria(filter.getCriteria())
                    .sorts(filter.getSorts());
        }
    }
}
