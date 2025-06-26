package team.blackhole.bot.asky.events.hooks;

import lombok.RequiredArgsConstructor;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import team.blackhole.bot.asky.events.AbstractEvent;

/**
 * Реализация хука
 */
@RequiredArgsConstructor
public class HookImpl implements Hook {

    /** Контекст точки входа в функцию обработки хука */
    private final Context context;

    /** Точка входа в функцию обработки хука */
    private final Value entryPoint;

    @Override
    public void handle(AbstractEvent event) {
        entryPoint.execute(context.asValue(event));
    }

    @Override
    public void close() {
        context.close();
    }
}
