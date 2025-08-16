package team.blackhole.bot.asky.executable;

/**
 * Исполняемое действие
 */
public interface Executable {

    /**
     * Выполняет заданное действие
     * @param params параметры исполнения
     */
    Object accept(Object ...params);
}
