package team.blackhole.bot.asky.handling.message.command.annotation;

import java.lang.annotation.*;

/**
 * Аннотация действия, перед выполнением команды
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BeforeCommand {
}
