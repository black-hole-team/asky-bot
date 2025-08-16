package team.blackhole.bot.asky.executable;

import lombok.RequiredArgsConstructor;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

/**
 * Реализация {@link Executable} на основе скрипта
 */
@RequiredArgsConstructor
public class ExecutableImpl implements Executable {

    /** Контекст точки входа в функцию обработки хука */
    private final Context context;

    /** Точка входа в функцию обработки хука */
    private final Value entryPoint;

    @Override
    public Object accept(Object... params) {
        Value[] values = new Value[params.length];
        for (int i = 0; i < params.length; i++) {
            values[i] = context.asValue(params[i]);
        }
        return entryPoint.execute((Object[]) values);
    }
}
