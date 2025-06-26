package team.blackhole.bot.asky.handling.command.callback.handlers;

import com.google.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import team.blackhole.bot.asky.channel.ChannelCallback;
import team.blackhole.bot.asky.channel.sending.MessageSender;
import team.blackhole.bot.asky.config.AskyHandlingConfiguration;
import team.blackhole.bot.asky.db.jedis.domain.Stage;
import team.blackhole.bot.asky.handling.StageName;
import team.blackhole.bot.asky.handling.command.CommandContext;
import team.blackhole.bot.asky.handling.command.annotations.Command;
import team.blackhole.bot.asky.handling.command.callback.ChannelCallbackCommandHandler;
import team.blackhole.bot.asky.handling.command.filtering.FilterHelperFactory;
import team.blackhole.bot.asky.handling.command.filtering.PageRenderer;
import team.blackhole.bot.asky.handling.command.filtering.PageRendererFactoryProvider;
import team.blackhole.bot.asky.security.AskyUserRole;

/**
 * Обработчик данных обратного вызова на стадии {@link StageName#NONE}
 */
@Log4j2
@RequiredArgsConstructor(onConstructor_ = @__(@Inject))
public class NoneCallbackHandler extends ChannelCallbackCommandHandler {

    /** Идентификатор параметра префикса */
    public static final String PREFIX_COMMAND_PARAM = "prefix";

    /** Идентификатор параметра действия */
    public static final String ACTION_COMMAND_PARAM = "action";

    /** Конфигурация обработки сообщений */
    private final AskyHandlingConfiguration handlingConfiguration;

    /** Отправитель сообщений */
    private final MessageSender messageSender;

    /** Поставщик фабрик отрисовщика страниц сущностей */
    private final PageRendererFactoryProvider pageRendererFactoryProvider;

    /** Помощник для работы с фильтром */
    private final FilterHelperFactory filterHelperFactory;

    /**
     * Обрабатывает все сообщения на текущей стадии
     * @param stage   стадия
     * @param context контекст
     */
    @Transactional
    @Command(value = "^page:(?<" + PREFIX_COMMAND_PARAM + ">[^:]+):(?<" + ACTION_COMMAND_PARAM + ">" + PageRenderer.NEXT_ACTION + "|" + PageRenderer.PREV_ACTION + ")$",
            role = {AskyUserRole.ADMIN, AskyUserRole.OPERATOR})
    public Stage onPage(Stage stage, ChannelCallback callback, CommandContext context) {
        var newStage = Stage.propagation(stage, false);
        var prefix = (String) context.get(PREFIX_COMMAND_PARAM);
        var action = (String) context.get(ACTION_COMMAND_PARAM);
        var helper = filterHelperFactory.create(prefix, newStage);
        switch (action) {
            case PageRenderer.NEXT_ACTION -> helper.incrementPage(1);
            case PageRenderer.PREV_ACTION -> helper.incrementPage(-1);
        }
        messageSender.edit(callback.channelId(), callback.chatId(), callback.messageId(), pageRendererFactoryProvider.create(helper).create());
        return newStage;
    }

    @Override
    protected AskyHandlingConfiguration getHandlingConfiguration() {
        return handlingConfiguration;
    }
}
