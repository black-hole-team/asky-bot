package team.blackhole.bot.asky.handling.command.annotation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Контекст выполнения команды
 */
public class CommandContext {

    /** Карта, где ключ это класс данных, а значение это данные */
    private final Map<Class<?>, Supplier<?>> contextDataProviders = new HashMap<>();

    /**
     * Регистрирует нового поставщика данных в контексте
     * @param type     класс типа данных
     * @param provider поставщик данных
     * @param <T> тип данных
     */
    public <T> void register(Class<T> type, Supplier<T> provider) {
        this.contextDataProviders.put(type, provider);
    }

    /**
     * Возвращает значение по типу
     * @param type тип
     * @return значение
     * @param <T> тип значения
     */
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> type) {
        var supplier = (Supplier<T>) this.contextDataProviders.get(type);
        if (supplier == null) {
            return null;
        }
        return supplier.get();
    }
}
