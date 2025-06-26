package team.blackhole.bot.asky.handling.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Контекст выполнения команды
 */
@RequiredArgsConstructor
public class CommandContext {

    /** Карта, где ключ это класс данных, а значение это данные */
    private final Map<Class<?>, Supplier<?>> contextDataProviders = new HashMap<>();

    /** Текстовое значение команды */
    @Getter
    private final String command;

    /** Аргументы сообщения */
    private final Map<String, Object> arguments = new HashMap<>();

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
     * Добавляет аргумент к контексту сообщения
     * @param name  наименование аргумента
     * @param value значение аргумента
     */
    public void argument(String name, Object value) {
        this.arguments.put(name, value);
    }

    /**
     * Возвращает значение аргумента из контекста сообщений
     * @param name наименование аргумента
     * @return значение аргумента
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String name) {
        return (T) this.arguments.get(name);
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
