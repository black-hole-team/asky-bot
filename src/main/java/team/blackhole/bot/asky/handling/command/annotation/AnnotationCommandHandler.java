package team.blackhole.bot.asky.handling.command.annotation;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import team.blackhole.bot.asky.channel.ChannelMessage;
import team.blackhole.bot.asky.channel.ChannelMessageSource;
import team.blackhole.bot.asky.db.jedis.domain.Stage;
import team.blackhole.bot.asky.handling.command.CommandHandler;
import team.blackhole.bot.asky.handling.command.CommandScope;
import team.blackhole.bot.asky.handling.events.MessageEvent;
import team.blackhole.bot.asky.security.AskyUser;
import team.blackhole.bot.asky.security.AskyUserRole;
import team.blackhole.bot.asky.support.ReflexionUtils;
import team.blackhole.bot.asky.support.exception.AskyException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Обработчик команд на основе аннотаций
 */
public abstract class AnnotationCommandHandler implements CommandHandler {

    /** Обработчик любой команды */
    public static final String ANY_COMMAND = "*";

    /** Карта, где ключ это команда, а значение это обработчик команды */
    private final Map<String, CommandHandler> commands = new HashMap<>();

    /** Функции выполняемые перед обработкой сообщения */
    private final List<Consumer<CommandContext>> beforeFunctions = new ArrayList<>();

    @Override
    public Stage handle(AskyUser user, Stage stage, MessageEvent event) {
        var message = event.getMessage();
        var command = getCommand(message.content());
        var handler = commands.get(command);
        if (handler == null) {
            handler = commands.get(ANY_COMMAND);
        }
        return handler == null ? stage : handler.handle(user, stage, event);
    }

    /**
     * Регистрирует новую команду и её обработчик
     * @param command команда (например, "/start")
     * @param handler функция-обработчик команды
     */
    protected void registerCommand(String command, CommandHandler handler) {
        commands.put(command, handler);
    }

    /**
     * Регистрирует функции выполняемые перед обработкой сообщения
     * @param beforeFunction функции выполняемые перед обработкой сообщения
     */
    protected void registerBefore(Consumer<CommandContext> beforeFunction) {
        this.beforeFunctions.add(beforeFunction);
    }

    /**
     * Извлекает первое слово из строки (команду).
     * @param message входное сообщение
     * @return Первое слово или пустую строку, если сообщение пустое
     */
    private static String getCommand(String message) {
        if (message == null || message.isBlank()) {
            return "";
        }

        // Убираем лишние пробелы и разбиваем по пробелам
        var trimmed = message.trim();
        int firstSpaceIndex = Stream.of(trimmed.indexOf(' '), trimmed.indexOf('@'))
                .filter(e -> e != -1)
                .findFirst()
                .orElse(-1);

        // Если пробела нет, возвращаем всю строку
        if (firstSpaceIndex == -1) {
            return trimmed;
        }

        // Иначе берем подстроку до первого пробела
        return trimmed.substring(0, firstSpaceIndex);
    }

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
                registerCommand(command.value(), new SecuredCommandHandler(command, Set.of(command.role()), Set.of(command.scopes()),
                        method, method.getParameters()));
            }
        }
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
        public void accept(CommandContext commandContext) {
            try {
                method.invoke(AnnotationCommandHandler.this, getParams(commandContext));
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

        /** Список областей действия */
        private final Set<CommandScope> scopes;

        /** Метод */
        private final Method method;

        /** Параметры */
        private final Parameter[] parameters;

        @Override
        public Stage handle(AskyUser user, Stage stage, MessageEvent event) {
            var message = event.getMessage();
            if (!canAccess(user, message)) {
                throw new AskyException("Доступ запрещен");
            }
            try {
                return (Stage) method.invoke(AnnotationCommandHandler.this, getParams(user, stage, event));
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new AskyException("Ошибка при вызове метода команды '%s'".formatted(command.value()), e);
            }
        }

        /**
         * Проверяет возможность пользователя доступа к команде
         * @param user    пользователь
         * @param message исходное сообщение
         * @return {@code true}, если доступ к команде возможен, {@code false}, если иначе
         */
        private boolean canAccess(AskyUser user, ChannelMessage message) {
            if (!roles.contains(user.role())) {
                return false;
            }
            if (message.source() == ChannelMessageSource.CHAT) {
                return scopes.contains(CommandScope.COMMON);
            }
            if (message.topicId() == null) {
                return scopes.contains(CommandScope.HUB) && message.source() == ChannelMessageSource.GROUP;
            } else {
                return scopes.contains(CommandScope.TOPIC);
            }
        }

        /**
         * Возвращает параметры для вызова метода
         * @param user  пользователь
         * @param stage стадия
         * @param event событие сообщения
         * @return параметры для вызова метода
         */
        private Object[] getParams(AskyUser user, Stage stage, MessageEvent event) {
            var context = getCommandContext(user, stage, event);
            for (var current : beforeFunctions) {
                current.accept(context);
            }
            var params = new Object[parameters.length];
            for (var i = 0; i < parameters.length; i++) {
                params[i] = context.get(parameters[i].getType());
            }
            return params;
        }

        /**
         * Возвращает контекст команды
         * @param user  пользователь
         * @param stage стадия
         * @param event событие сообщения
         * @return контекст команды
         */
        @NotNull
        private static CommandContext getCommandContext(AskyUser user, Stage stage, MessageEvent event) {
            var context = new CommandContext();
            context.register(AskyUser.class, () -> user);
            context.register(Stage.class, () -> stage);
            context.register(MessageEvent.class, () -> event);
            context.register(CommandContext.class, () -> context);
            return context;
        }
    }
}
