package team.blackhole.bot.asky.handling.hooks;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import team.blackhole.bot.asky.channel.ChannelPool;
import team.blackhole.bot.asky.support.exception.AskyException;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Реализация фабрики хуков {@link HookFactory}
 */
@RequiredArgsConstructor(onConstructor_ = @__(@Inject))
public class HookFactoryImpl implements HookFactory {

    /** Пул каналов */
    private final ChannelPool channelPool;

    @Override
    public Hook create(Path pathToSourceFile) {
        try {
            var sourceFile = pathToSourceFile.toFile();
            var lang = Source.findLanguage(sourceFile);
            var context = createContext(pathToSourceFile, lang);
            var bindings = context.getBindings(lang);

            bindings.putMember("logger", LogManager.getLogger(sourceFile.getName()));
            bindings.putMember("pool", channelPool);

            context.eval(Source.newBuilder(lang, sourceFile).build());

            return new HookImpl(context, bindings.getMember("handle"));
        } catch (IOException e) {
            throw new AskyException("Ошибка создания хука из файла '%s'".formatted(pathToSourceFile), e);
        }
    }

    /**
     * Создаёт контекст для выполнения скрипта
     * @param pathToSourceFile путь до файла источника
     * @param lang             язык скрипта
     * @return созданный контекст
     */
    private static Context createContext(Path pathToSourceFile, String lang) {
        return Context.newBuilder(lang)
                .allowAllAccess(true)
                .currentWorkingDirectory(pathToSourceFile.getParent().toAbsolutePath())
                .build();
    }
}
