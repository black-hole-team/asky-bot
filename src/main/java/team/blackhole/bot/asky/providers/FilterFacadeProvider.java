package team.blackhole.bot.asky.providers;

import com.google.inject.Provider;
import team.blackhole.data.filter.FilterFacade;
import team.blackhole.data.filter.FilterFacadeImpl;
import team.blackhole.data.filter.builder.FilterBuilder;
import team.blackhole.data.filter.builder.FilterBuilderFactory;
import team.blackhole.data.filter.builder.FilterBuilderImpl;
import team.blackhole.data.filter.writer.FilterWriterImpl;

/**
 * Поставщик фасада фильтрации
 */
public class FilterFacadeProvider implements Provider<FilterFacade> {

    @Override
    public FilterFacade get() {
        return new FilterFacadeImpl(new FilterWriterImpl(), new AskyFilterBuilderFactory());
    }

    /**
     * Фабрика построителя фильтров чата
     */
    public static class AskyFilterBuilderFactory implements FilterBuilderFactory {

        @Override
        public FilterBuilder create() {
            return new FilterBuilderImpl()
                    .page(0)
                    .pageSize(10);
        }
    }
}
