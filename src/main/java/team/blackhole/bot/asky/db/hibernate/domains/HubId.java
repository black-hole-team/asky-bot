package team.blackhole.bot.asky.db.hibernate.domains;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import javax.annotation.Nonnull;

/**
 * Идентификатор хаба в канале обработки сообщений
 */
@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class HubId {

    /** Идентификатор хаба */
    @Nonnull
    private long id;

    /** Идентификатор канала, на котором работает хаб */
    @Nonnull
    @Column(name = "channel_id")
    private String channelId;
}
