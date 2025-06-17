package team.blackhole.bot.asky.db.hibernate;

/**
 * Постоянная сущность
 */
public interface PersistentEntity {

    /**
     * Возвращает признак новой (ещё не сохраненной) сущности
     * @return признак новой, ещё не сохраненной
     */
    boolean isNew();
}
