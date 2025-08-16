package team.blackhole.bot.asky.handling.command;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import team.blackhole.bot.asky.channel.ChannelEntity;
import team.blackhole.bot.asky.channel.ChannelMessage;
import team.blackhole.bot.asky.channel.ChannelMessageSource;
import team.blackhole.bot.asky.config.AskyHandlingConfiguration;
import team.blackhole.bot.asky.db.jedis.domain.Stage;
import team.blackhole.bot.asky.handling.ChannelEntityHandler;
import team.blackhole.bot.asky.handling.command.annotations.BeforeCommand;
import team.blackhole.bot.asky.handling.command.annotations.Command;
import team.blackhole.bot.asky.security.AskyUser;
import team.blackhole.bot.asky.security.AskyUserRole;
import team.blackhole.bot.asky.support.ReflexionUtils;
import team.blackhole.bot.asky.support.exception.AskyException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * Реализация обработчика на основе шаблонов регулярных выражений и аннотаций
 */
public abstract class AbstractCommandHandler<T extends ChannelEntity> implements ChannelEntityHandler<T> {

    /** Обработчик любой команды */
    public static final String ANY_COMMAND = ".*";

    /** Набор вхождений обработчиков команд и шаблонов команд, отсортированных по длине команды */
    private final Set<CommandHandlerEntry> commands = new TreeSet<>(Comparator.comparingInt(e -> -e.pattern.pattern().length()));

    /** Функции выполняемые перед обработкой сообщения */
    private final List<Consumer<CommandContext>> beforeFunctions = new ArrayList<>();

    // Инициализатор
    {
        var methods = ReflexionUtils.classOfObject(this).getDeclaredMethods();
        for (var method : methods) {
            var before = method.getDeclaredAnnotation(BeforeCommand.class);
            if (before != null) {
                registerBefore(new BeforeCommandHandler(method, method.getParameters()));
            }
            var command = method.getDeclaredAnnotation(Command.class);
            if (command != null) {
                register(Pattern.compile(command.value()), new SecuredCommandHandler(command, Set.of(command.role()), method, method.getParameters()));
            }
        }
    }

    @Override
    public Stage handle(Stage stage, T entity) {
        var content = getEntityContent(entity);
        if (content == null || content.isBlank()) {
            return Stage.propagation(stage, false);
        }
        for (var entry : commands) {
            var pattern = entry.pattern();
            var matcher = pattern.matcher(content);
            if (matcher.find()) {
                var context = getCommandContext(matcher.group(), stage, entity);
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
     * Возвращает содержимое сущности канала
     * @param entity сущность канала
     * @return содержимое сущности канала
     */
    protected abstract String getEntityContent(T entity);

    /**
     * Регистрирует новую команду и её обработчик
     * @param command шаблон команды (например, "/start")
     * @param handler функция-обработчик команды
     */
    protected void register(Pattern command, CommandHandler handler) {
        commands.add(new CommandHandlerEntry(command, handler));
    }

    /**
     * Регистрирует функции выполняемые перед обработкой сообщения
     * @param beforeFunction функции выполняемые перед обработкой сообщения
     */
    protected void registerBefore(Consumer<CommandContext> beforeFunction) {
        this.beforeFunctions.add(beforeFunction);
    }

    /**
     * Возвращает контекст команды
     * @param command команда
     * @param stage   стадия
     * @return контекст команды
     */
    @NotNull
    protected CommandContext getCommandContext(String command, Stage stage, T entity) {
        var context = new CommandContext(command);
        context.register(AskyUser.class, () -> getUser(entity));
        context.register(Stage.class, () -> stage);
        context.register(CommandContext.class, () -> context);
        return context;
    }

    /**
     * Возвращает пользователя отправителя сообщения
     * @param entity сущность канала
     * @return пользователь отправитель сообщения
     */
    private AskyUser getUser(T entity) {
        return new AskyUser(entity.userId(), getHandlingConfiguration().getRoles().get(entity.channelId()).getRole(entity.userId()));
    }

    /**
     * Вхождение обработчика команды с шаблоном команды
     * @param pattern шаблон
     * @param handler обработчик команды
     */
    private record CommandHandlerEntry(Pattern pattern, CommandHandler handler) {

    }

    /**
     * Действие выполняемое перед обработкой команды
     */
    @RequiredArgsConstructor
    private class BeforeCommandHandler implements Consumer<CommandContext> {

        /** Метод */
        private final Method method;

        /** Параметры */
        private final Parameter[] parameters;

        @Override
        public void accept(CommandContext context) {
            try {
                method.invoke(AbstractCommandHandler.this, getParams(context));
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new AskyException("Ошибка при вызове метода '%s'".formatted(method.getName()), e);
            }
        }

        /**
         * Возвращает параметры для вызова метода
         * @param context контекст команды
         * @return параметры для вызова метода
         */
        private Object[] getParams(CommandContext context) {
            var params = new Object[parameters.length];
            for (var i = 0; i < parameters.length; i++) {
                params[i] = context.get(parameters[i].getType());
            }
            return params;
        }
    }

    /**
     * Защищенный обработчик команд
     */
    @RequiredArgsConstructor
    private class SecuredCommandHandler implements CommandHandler {

        /** Команда */
        private final Command command;

        /** Список ролей */
        private final Set<AskyUserRole> roles;

        /** Метод */
        private final Method method;

        /** Параметры */
        private final Parameter[] parameters;

        @Override
        public Stage handle(CommandContext context) {
            if (!canAccess(context.get(AskyUser.class))) {
                throw new AskyException("Доступ запрещен");
            }
            try {
                var result = method.invoke(AbstractCommandHandler.this, getParams(context));
                if (result == null) {
                    return Stage.propagation(context.get(Stage.class), false);
                }
                return (Stage) result;
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new AskyException("Ошибка при вызове метода команды '%s'".formatted(command.value()), e.getCause());
            }
        }

        /**
         * Проверяет возможность пользователя доступа к команде
         * @param user пользователь
         * @return {@code true}, если доступ к команде возможен, {@code false}, если иначе
         */
        private boolean canAccess(AskyUser user) {
            return roles.contains(user.role());
        }

        /**
         * Возвращает параметры для вызова метода
         * @param context контекст
         * @return параметры для вызова метода
         */
        private Object[] getParams(CommandContext context) {
            for (var current : beforeFunctions) {
                current.accept(context);
            }
            var params = new Object[parameters.length];
            for (var i = 0; i < parameters.length; i++) {
                params[i] = context.get(parameters[i].getType());
            }
            return params;
        }
    }
}
