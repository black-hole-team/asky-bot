package team.blackhole.bot.asky.events.hooks;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Provider;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import team.blackhole.bot.asky.config.AskyExecutableConfiguration;
import team.blackhole.bot.asky.config.AskyExecutableHooksConfiguration;
import team.blackhole.bot.asky.events.AbstractEvent;
import team.blackhole.bot.asky.support.ApplicationHelper;
import team.blackhole.bot.asky.support.exception.AskyException;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Поставщик движка выполнения хуков
 */
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class HookEngineProvider implements Provider<HookEngine> {

    /** Шина передачи событий */
    private final EventBus eventBus;

    /** Фабрика хуков */
    private final HookFactory hookFactory;

    /** Конфигурация хуков */
    private final AskyExecutableConfiguration executableConfiguration;

    @Override
    public HookEngine get() {
        var engine = new HookEngine(hooks());
        eventBus.register(engine);
        return engine;
    }

    /**
     * Возвращает карту соответствия типа события и хука
     * @return карта, где ключ это тип события хука, а значение это массив хуков
     */
    private Map<Class<? extends AbstractEvent>, Hook[]> hooks() {
        var result = new HashMap<Class<? extends AbstractEvent>, Hook[]>();
        for (var current : HookType.values()) {
            log.info("Регистрация хуков типа '{}'", current);
            var hookDir = executableConfiguration.getHooks().getDir().resolve(current.name().toLowerCase());
            if (!Files.exists(hookDir)) {
                log.info("Хуки типа '{}' не найдены", current);
                continue;
            }
            var hooks = new ArrayList<Hook>();
            try (var stream = Files.newDirectoryStream(hookDir)) {
                for (var file : stream) {
                    if (Files.isDirectory(file)) {
                        continue;
                    }
                    log.info("Регистрация файла хука '{}'", file);
                    hooks.add(hookFactory.create(file.toAbsolutePath()));
                    log.info("Файл хука '{}' успешно зарегистрирован", file);
                }
            } catch (IOException e) {
                throw new AskyException("Ошибка при чтении директории хуков", e);
            }
            result.put(current.getEventType(), hooks.toArray(Hook[]::new));
            log.info("Хуки типа '{}' успешно зарегистрированы", current);
        }
        return result;
    }
}
