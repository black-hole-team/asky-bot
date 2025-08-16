package team.blackhole.bot.asky.events.hooks;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import team.blackhole.bot.asky.channel.ChannelPool;
import team.blackhole.bot.asky.executable.ExecutableFactory;
import team.blackhole.bot.asky.support.exception.AskyException;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Реализация фабрики хуков {@link HookFactory}
 */
@RequiredArgsConstructor(onConstructor_ = @__(@Inject))
public class HookFactoryImpl implements HookFactory {

    /** Фабрика исполняемых на стороне скриптов */
    private final ExecutableFactory executableFactory;

    @Override
    public Hook create(Path pathToSourceFile) {
        return new HookImpl(executableFactory.create(pathToSourceFile, "handle"));
    }
}
