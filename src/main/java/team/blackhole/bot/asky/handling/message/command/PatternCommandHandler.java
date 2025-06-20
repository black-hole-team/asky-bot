package team.blackhole.bot.asky.handling.message.command;

import org.jetbrains.annotations.NotNull;
import team.blackhole.bot.asky.channel.ChannelMessage;
import team.blackhole.bot.asky.config.AskyHandlingConfiguration;
import team.blackhole.bot.asky.db.jedis.domain.Stage;
import team.blackhole.bot.asky.handling.message.MessageHandler;
import team.blackhole.bot.asky.security.AskyUser;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * Реализация обработчика команд на основе шаблонов регулярных выражений
 */
public abstract class PatternCommandHandler implements MessageHandler {

    /** Обработчик любой команды */
    public static final String ANY_COMMAND = ".*";

    /** Набор вхождений обработчиков команд и шаблонов команд, отсортированных по длине команды */
    private final Set<CommandHandlerEntry> commands = new TreeSet<>(Comparator.comparingInt(e -> -e.pattern.pattern().length()));

    @Override
    public Stage handle(Stage stage, ChannelMessage message) {
        var content = message.content();
        if (content == null || content.isBlank()) {
            return Stage.propagation(stage, false);
        }
        for (var entry : commands) {
            var pattern = entry.pattern();
            var matcher = pattern.matcher(content);
            if (matcher.find()) {
                var context = getCommandContext(matcher.group(), stage, message);
                for (var group : matcher.namedGroups().entrySet()) {
                    context.argument(group.getKey(), matcher.group(group.getKey()));
                }
                return entry.handler().handle(context);
            }
        }
        return Stage.propagation(stage, false);
    }

    /**
     * Возвращает конфигурацию обработки сообщений
     * @return конфигурация обработки сообщений
     */
    protected abstract AskyHandlingConfiguration getHandlingConfiguration();

    /**
     * Регистрирует новую команду и её обработчик
     * @param command шаблон команды (например, "/start")
     * @param handler функция-обработчик команды
     */
    protected void registerCommand(Pattern command, CommandHandler handler) {
        commands.add(new CommandHandlerEntry(command, handler));
    }

    /**
     * Возвращает контекст команды
     * @param command команда
     * @param stage   стадия
     * @param message сообщение
     * @return контекст команды
     */
    @NotNull
    protected CommandContext getCommandContext(String command, Stage stage, ChannelMessage message) {
        var context = new CommandContext(command);
        context.register(AskyUser.class, () -> getUser(message));
        context.register(Stage.class, () -> stage);
        context.register(ChannelMessage.class, () -> message);
        context.register(CommandContext.class, () -> context);
        return context;
    }

    /**
     * Возвращает пользователя отправителя сообщения
     * @param message сообщение
     * @return пользователь отправитель сообщения
     */
    private AskyUser getUser(ChannelMessage message) {
        return new AskyUser(message.userId(), getHandlingConfiguration().getRoles().get(message.channelId()).getRole(message.userId()));
    }

    /**
     * Вхождение обработчика команды с шаблоном команды
     * @param pattern шаблон
     * @param handler обработчик команды
     */
    private record CommandHandlerEntry(Pattern pattern, CommandHandler handler) {

    }
}
