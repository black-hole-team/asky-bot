package team.blackhole.bot.asky.handling.message.command.annotation;

import team.blackhole.bot.asky.handling.message.command.CommandScope;
import team.blackhole.bot.asky.security.AskyUserRole;

import java.lang.annotation.*;

/**
 * Аннотация команды
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Command {

    /**
     * Возвращает команду
     * @return команда
     */
    String value() default AnnotationCommandHandler.ANY_COMMAND;

    /**
     * Возвращает роли пользователей, которым разрешено выполнять команду
     * @return роли пользователей, которым разрешено выполнять команду
     */
    AskyUserRole[] role() default {AskyUserRole.ADMIN, AskyUserRole.COMMON, AskyUserRole.OPERATOR};

    /**
     * Возвращает области действия команды
     * @return массив областей действия команды
     */
    CommandScope[] scopes() default {CommandScope.TOPIC, CommandScope.COMMON, CommandScope.HUB};
}
