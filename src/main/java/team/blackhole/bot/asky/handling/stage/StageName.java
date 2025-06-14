package team.blackhole.bot.asky.handling.stage;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Наименование стадии
 */
@Getter
@AllArgsConstructor
public enum StageName {

    /** Без стадии */
    NONE(false),

    /** Регистрация хаба */
    HUB_REGISTRATION(true);

    /** Признак возможности отмены стадии */
    private final boolean canBeCanceled;
}
