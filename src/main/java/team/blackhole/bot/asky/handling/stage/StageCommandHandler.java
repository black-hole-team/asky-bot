package team.blackhole.bot.asky.handling.stage;

import lombok.RequiredArgsConstructor;
import team.blackhole.bot.asky.channel.ChannelMessage;
import team.blackhole.bot.asky.db.jedis.domain.Stage;
import team.blackhole.bot.asky.handling.events.MessageEvent;
import team.blackhole.bot.asky.security.AskyUser;
import team.blackhole.bot.asky.security.AskyUserRole;
import team.blackhole.bot.asky.support.ReflexionUtils;
import team.blackhole.bot.asky.support.exception.AskyException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Обработчик стадии на основе команд
 */
public abstract class StageCommandHandler implements StageHandler {

    /** Обработчик любой команды */
    public static final String ANY_COMMAND = "*";

    /** Карта, где ключ это команда, а значение это обработчик команды */
    private final Map<String, StageHandler> commands = new HashMap<>();

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
    protected void registerCommand(String command, StageHandler handler) {
        commands.put(command, handler);
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
            var command = method.getDeclaredAnnotation(StageCommand.class);
            if (command == null) {
                continue;
            }
            registerCommand(command.value(), new SecuredStageHandler(command, Set.of(command.role()), Set.of(command.scopes()),
                    method, method.getParameters()));
        }
    }

    /**
     * Защищенный обработчик команд
     */
    @RequiredArgsConstructor
    private class SecuredStageHandler implements StageHandler {

        /** Команда */
        private final StageCommand command;

        /** Список ролей */
        private final Set<AskyUserRole> roles;

        /** Список областей действия */
        private final Set<StageCommandScope> scopes;

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
                return (Stage) method.invoke(StageCommandHandler.this, getParams(user, stage, event));
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
            if (message.chatId() > 0) {
                return scopes.contains(StageCommandScope.COMMON);
            }
            if (message.topicId() == null) {
                return scopes.contains(StageCommandScope.HUB) && message.chatId() < 0;
            } else {
                return scopes.contains(StageCommandScope.TOPIC);
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
            var params = new Object[parameters.length];
            for (var i = 0; i < parameters.length; i++) {
                var current = parameters[i];
                if (current.getType().isAssignableFrom(AskyUser.class)) {
                    params[i] = user;
                } else if (current.getType().isAssignableFrom(Stage.class)) {
                    params[i] = stage;
                } else if (current.getType().isAssignableFrom(MessageEvent.class)) {
                    params[i] = event;
                }
            }
            return params;
        }
    }
}
