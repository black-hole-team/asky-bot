package team.blackhole.bot.asky.handling.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Наименование стадии
 */
@Getter
@AllArgsConstructor
public enum StageName {

    /** Без стадии */
    NONE(false);

    /** Признак возможности отмены стадии */
    private final boolean canBeCanceled;
}
