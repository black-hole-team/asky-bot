package team.blackhole.bot.asky.handling.command.filtering;

import com.google.inject.Inject;
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
 * Фабрика помощников для работы с фильтром
 */
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class FilterHelperFactory {

    /** Фасад фильтрации */
    private final FilterFacade filterFacade;

    /**
     * Создает помощника для работы с фильтром
     * @param prefix префикс фильтра
     * @param stage  стадия обработки сообщения
     * @return помощник для работы с фильтром
     */
    public FilterHelper create(String prefix, Stage stage) {
        return new FilterHelperImpl(prefix, stage, filterFacade);
    }
}
