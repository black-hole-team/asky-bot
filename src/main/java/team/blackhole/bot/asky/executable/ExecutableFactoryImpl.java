package team.blackhole.bot.asky.executable;

import com.google.inject.Inject;
import com.google.inject.Provider;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import team.blackhole.bot.asky.channel.ChannelPool;
import team.blackhole.bot.asky.config.AskyExecutableConfiguration;
import team.blackhole.bot.asky.support.exception.AskyException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Реализация фабрики {@link ExecutableFactory} по умолчанию
 */
@RequiredArgsConstructor(onConstructor_ = @__(@Inject))
public class ExecutableFactoryImpl implements ExecutableFactory, AutoCloseable {

    /** Пул каналов */
    private final Provider<ChannelPool> channelPool;

    /** Конфигурация исполняемых на стороне сценариев */
    private final AskyExecutableConfiguration askyExecutableConfiguration;

    /** Карта соответствия в формате [путь до исполняемого файла - язык]: контекст */
    private final Map<String, Context> pathAndLangToContext = new HashMap<>();

    @Override
    public Executable create(Path path, String name) {
        var absolutePath = path.isAbsolute() ? path : askyExecutableConfiguration.getDir().resolve(path);
        try {
            var lang = Source.findLanguage(absolutePath.toFile());
            var context = getOrCreateContext(absolutePath, lang);
            return new ExecutableImpl(context, context.getBindings(lang).getMember(name));
        } catch (IOException e) {
            throw new AskyException("Ошибка определения языка исполняемого файла сценария '%s'".formatted(absolutePath), e);
        }
    }

    /**
     * Создаёт контекст для выполнения скрипта
     * @param pathToSourceFile путь до файла источника
     * @param lang             язык скрипта
     * @return созданный контекст
     */
    private Context getOrCreateContext(Path pathToSourceFile, String lang) {
        return pathAndLangToContext.computeIfAbsent(pathToSourceFile.toAbsolutePath() + lang, key -> {
            try {
                var context = Context.newBuilder(lang)
                        .allowAllAccess(true)
                        .currentWorkingDirectory(pathToSourceFile.getParent().toAbsolutePath())
                        .build();

                var bindings = context.getBindings(lang);
                var sourceFile = pathToSourceFile.toFile();

                bindings.putMember("LOGGER", LogManager.getLogger(sourceFile.getName()));
                bindings.putMember("POOL", channelPool.get());

                context.eval(Source.newBuilder(lang, sourceFile).build());

                return context;
            } catch (IOException e) {
                throw new AskyException("Ошибка инициализации исполняемого сценария из файла '%s'".formatted(pathToSourceFile), e);
            }
        });
    }

    @Override
    public void close() {
        for (var value : pathAndLangToContext.values()) {
            value.close();
        }
    }
}
