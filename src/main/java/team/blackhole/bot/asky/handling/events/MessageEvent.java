package team.blackhole.bot.asky.handling.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import team.blackhole.bot.asky.channel.ChannelMessage;

/**
 * Событие сообщения
 */
@Getter
@ToString
@RequiredArgsConstructor
public class MessageEvent extends AbstractEvent {

    /** Сообщение */
    private final ChannelMessage message;
}
