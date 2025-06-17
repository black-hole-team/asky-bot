package team.blackhole.bot.asky.handling.command;

import com.google.inject.Inject;
import com.google.inject.Provider;
import lombok.RequiredArgsConstructor;
import team.blackhole.bot.asky.config.AskyHandlingConfiguration;
import team.blackhole.bot.asky.db.jedis.repository.StageRepository;
import team.blackhole.bot.asky.handling.command.handlers.NoneHandler;

/**
 * Поставщик менеджера стадий
 */
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class CommandHandlerManagerProvider implements Provider<CommandHandlerManager> {

    /** Репозитория для работы со стадией */
    private final StageRepository stageRepository;

    /** Конфигурация обработки сообщений */
    private final AskyHandlingConfiguration handlingConfiguration;

    /** Обработчик стадии {@link StageName#NONE} */
    private final NoneHandler noneStageHandler;

    @Override
    public CommandHandlerManager get() {
        var manager = new CommandHandlerManager(stageRepository, handlingConfiguration);
        manager.register(StageName.NONE, noneStageHandler);
        return manager;
    }
}
