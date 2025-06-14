package team.blackhole.bot.asky.handling.stage;

import team.blackhole.bot.asky.security.AskyUserRole;

import java.lang.annotation.*;

/**
 * Аннотация команды стадии
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface StageCommand {

    /**
     * Возвращает комменду
     * @return комманда
     */
    String value() default StageCommandHandler.ANY_COMMAND;

    /**
     * Возвращает роли пользователей, которым разрешено выполнять команду
     * @return роли пользователей, которым разрешено выполнять команду
     */
    AskyUserRole[] role() default {AskyUserRole.BAN, AskyUserRole.ADMIN, AskyUserRole.COMMON, AskyUserRole.OPERATOR};

    /**
     * Возвращает области действия команды
     * @return массив областей действия команды
     */
    StageCommandScope[] scopes() default {StageCommandScope.TOPIC, StageCommandScope.COMMON, StageCommandScope.HUB};
}
