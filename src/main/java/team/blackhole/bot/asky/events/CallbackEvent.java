package team.blackhole.bot.asky.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import team.blackhole.bot.asky.channel.ChannelCallback;

/**
 * Событие обратного вызова
 */
@Getter
@ToString
@RequiredArgsConstructor
public class CallbackEvent extends AbstractEvent {

    /** Данные обратного вызова */
    private final ChannelCallback callback;
}
