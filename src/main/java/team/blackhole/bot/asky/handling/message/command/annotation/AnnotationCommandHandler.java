package team.blackhole.bot.asky.handling.message.command.annotation;

import lombok.RequiredArgsConstructor;
import team.blackhole.bot.asky.channel.ChannelMessage;
import team.blackhole.bot.asky.channel.ChannelMessageSource;
import team.blackhole.bot.asky.db.jedis.domain.Stage;
import team.blackhole.bot.asky.handling.message.command.CommandContext;
import team.blackhole.bot.asky.handling.message.command.CommandHandler;
import team.blackhole.bot.asky.handling.message.command.CommandScope;
import team.blackhole.bot.asky.handling.message.command.PatternCommandHandler;
import team.blackhole.bot.asky.security.AskyUser;
import team.blackhole.bot.asky.security.AskyUserRole;
import team.blackhole.bot.asky.support.ReflexionUtils;
import team.blackhole.bot.asky.support.exception.AskyException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * Обработчик команд на основе аннотаций
 */
public abstract class AnnotationCommandHandler extends PatternCommandHandler {

    /** Функции выполняемые перед обработкой сообщения */
    private final List<Consumer<CommandContext>> beforeFunctions = new ArrayList<>();

    /**
     * Регистрирует функции выполняемые перед обработкой сообщения
     * @param beforeFunction функции выполняемые перед обработкой сообщения
     */
    protected void registerBefore(Consumer<CommandContext> beforeFunction) {
        this.beforeFunctions.add(beforeFunction);
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
                registerCommand(Pattern.compile(command.value()), new SecuredCommandHandler(command, Set.of(command.role()), Set.of(command.scopes()),
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
        public Stage handle(CommandContext context) {
            if (!canAccess(context.get(AskyUser.class), context.get(ChannelMessage.class))) {
                throw new AskyException("Доступ запрещен");
            }
            try {
                var result = method.invoke(AnnotationCommandHandler.this, getParams(context));
                if (result == null) {
                    return Stage.propagation(context.get(Stage.class), false);
                }
                return (Stage) result;
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
