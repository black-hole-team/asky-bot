package team.blackhole.bot.asky.events.hooks;

import lombok.RequiredArgsConstructor;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import team.blackhole.bot.asky.events.AbstractEvent;
import team.blackhole.bot.asky.executable.Executable;

/**
 * Реализация хука
 */
@RequiredArgsConstructor
public class HookImpl implements Hook {

    /** Исполняемый на стороне скрипт */
    private final Executable executable;

    @Override
    public void handle(AbstractEvent event) {
        executable.accept(event);
    }
}
