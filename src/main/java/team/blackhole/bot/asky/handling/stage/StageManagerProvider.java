package team.blackhole.bot.asky.handling.stage;

import com.google.inject.Inject;
import com.google.inject.Provider;
import lombok.RequiredArgsConstructor;
import team.blackhole.bot.asky.config.AskyHandlingConfiguration;
import team.blackhole.bot.asky.db.jedis.repository.StageRepository;
import team.blackhole.bot.asky.handling.stage.handlers.NoneStageHandler;

/**
 * Поставщик менеджера стадий
 */
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class StageManagerProvider implements Provider<StageManager> {

    /** Репозитория для работы со стадией */
    private final StageRepository stageRepository;

    /** Конфигурация обработки сообщений */
    private final AskyHandlingConfiguration handlingConfiguration;

    /** Обработчик стадии {@link team.blackhole.bot.asky.handling.stage.StageName#NONE} */
    private final NoneStageHandler noneStageHandler;

    @Override
    public StageManager get() {
        var manager = new StageManager(stageRepository, handlingConfiguration);
        manager.register(StageName.NONE, noneStageHandler);
        return manager;
    }
}
