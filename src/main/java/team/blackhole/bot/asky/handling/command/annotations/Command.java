package team.blackhole.bot.asky.handling.command.annotations;

import team.blackhole.bot.asky.handling.command.AbstractCommandHandler;
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
    String value() default AbstractCommandHandler.ANY_COMMAND;

    /**
     * Возвращает роли пользователей, которым разрешено выполнять команду
     * @return роли пользователей, которым разрешено выполнять команду
     */
    AskyUserRole[] role() default {AskyUserRole.ADMIN, AskyUserRole.COMMON, AskyUserRole.OPERATOR};
}
