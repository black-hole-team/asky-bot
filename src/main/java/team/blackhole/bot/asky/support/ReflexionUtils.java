package team.blackhole.bot.asky.support;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Инструменты для работы с рефлексией
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReflexionUtils {

    /**
     * Возвращает класс объекта
     * @param object объект
     * @return класс объекта
     * @param <T> тип объекта
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<? super T> classOfObject(T object) {
        if (object == null) {
            return null;
        }
        return skipProxyClasses((Class<T>) object.getClass());
    }

    /**
     * Пропускает прокси классы и возвращает исходный тип
     * @param type класс
     * @return исходный класс
     * @param <T> тип класса
     */
    public static <T> Class<? super T> skipProxyClasses(Class<T> type) {
        if (type.getSimpleName().contains("$$EnhancerByGuice$$")) {
            return skipProxyClasses(type.getSuperclass());
        }
        return type;
    }
}
