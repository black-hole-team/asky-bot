package team.blackhole.bot.asky.handling.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Событие в системе asky
 */
@Getter
@RequiredArgsConstructor
public class AbstractEvent {

    /** Признак отмененного события */
    private boolean canceled = false;

    /**
     * Отменяет событие
     */
    public void cancel() {
        canceled = true;
    }
}
