package team.blackhole.bot.asky.db.hibernate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import team.blackhole.bot.asky.support.exception.AskyException;
import team.blackhole.data.filter.*;

import java.util.*;

/**
 * Конвертер фильтра для hibernate
 */
@RequiredArgsConstructor
public class HibernateFilterConverter<T> {

    /** Менеджер сущностей */
    private final EntityManager entityManager;

    /** Класс сущности */
    private final Class<T> entityClass;

    /**
     * Конвертирует фильтр в критерии запроса hibernate
     * @param filter фильтр
     * @return критерии запроса
     */
    public CriteriaQuery<T> convert(Filter filter) {
        if (filter.getPageSize() <= 0) {
            throw new AskyException("Размер страницы должен быть больше нуля");
        }
        var cb = entityManager.getCriteriaBuilder();
        var cr = cb.createQuery(entityClass);
        var root = cr.from(entityClass);
        cr.where(filter.getCriteria().stream().map(criteria -> convertCriteria(criteria, cb, root)).toArray(Predicate[]::new));
        cr.orderBy(convertSort(filter.getSorts(), cb, root));
        return cr.select(root);
    }

    /**
     * Конвертирует сортировку фильтра в сортировку jpa
     * @param sorts сортировка фильтра
     * @return cортировка jpa
     */
    private Order[] convertSort(List<FilterSort> sorts, CriteriaBuilder cb, Root<T> root) {
        var orders = new Order[sorts.size()];
        for (int i = 0; i < sorts.size(); i++) {
            var filtered = sorts.get(i);
            orders[i] = filtered.getDirection() == SortDirection.ASC ?
                cb.asc(path(root, filtered.getField())) :
                cb.desc(path(root, filtered.getField()));
        }
        return orders;
    }

    /**
     * Конвертирует критерий фильтра в критерий jpa
     * @param criteria критерий
     * @return критерий jpa
     */
    @SuppressWarnings({"unchecked"})
    private Predicate convertCriteria(FilterCriteria criteria, CriteriaBuilder cb, Root<T> root) {
        switch (criteria) {
            case FilterGroupCriteria fgc -> {
                var subCriteria = fgc.getCriteria()
                        .stream()
                        .map(currentCriteria -> convertCriteria(currentCriteria, cb, root))
                        .toArray(Predicate[]::new);
                if (fgc.getOperator() == GroupOperator.AND) {
                    return cb.and(subCriteria);
                } else {
                    return cb.or(subCriteria);
                }
            }
            case FilterFieldCriteria ffc -> {
                return switch (ffc.getOperator()) {
                    case "=" -> {
                        if (ffc.getValue() == null) {
                            yield cb.isNull(path(root, ffc.getField()));
                        } else {
                            yield cb.equal(path(root, ffc.getField()), ffc.getValue());
                        }
                    }
                    case "!=" -> {
                        if (ffc.getValue() == null) {
                            yield cb.isNotNull(path(root, ffc.getField()));
                        } else {
                            yield cb.notEqual(path(root, ffc.getField()), ffc.getValue());
                        }
                    }
                    case ">" ->
                            cb.greaterThan(path(root, ffc.getField()), cb.literal((Comparable<Object>) ffc.getValue()));
                    case "<" ->
                            cb.lessThan(path(root, ffc.getField()), cb.literal((Comparable<Object>) ffc.getValue()));
                    case ">=" ->
                            cb.greaterThanOrEqualTo(path(root, ffc.getField()), cb.literal((Comparable<Object>) ffc.getValue()));
                    case "<=" ->
                            cb.lessThanOrEqualTo(path(root, ffc.getField()), cb.literal((Comparable<Object>) ffc.getValue()));
                    case "in" -> path(root, ffc.getField()).in((Collection<?>) ffc.getValue());
                    case "not in" -> cb.not(path(root, ffc.getField()).in((Collection<?>) ffc.getValue()));
                    case "like" -> cb.like(path(root, ffc.getField()), (String) ffc.getValue());
                    case "not like" -> cb.notLike(path(root, ffc.getField()), (String) ffc.getValue());
                    default -> throw new AskyException(String.format("Неизвестный оператор '%s'", ffc.getOperator()));
                };
            }
            case FilterStaticCriteria fsc -> {
                return cb.equal(cb.literal(1), cb.literal(fsc.isAllowFilter() ? 1 : 0));
            }
            default -> throw new AskyException(String.format("Неизвестный тип критерия %s.", criteria.getClass().getSimpleName()));
        }
    }

    /**
     * Возвращает путь к полю сущности
     * @param root данные сущности
     * @param name путь до поля
     * @return путь
     * @param <T> тип сущности
     * @param <R> тип поля
     */
    private static <T, R> Path<R> path(Root<T> root, String name) {
        var separated = name.split("\\.");
        var path = root.<R>get(separated[0]);
        for (var i = 1; i < separated.length; i++) {
            path = path.get(separated[i]);
        }
        return path;
    }
}
