package team.blackhole.bot.asky.db.hibernate.domains;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import javax.annotation.Nonnull;

/**
 * Идентификатор чата в канале обработки сообщений
 */
@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ChatId {

    /** Идентификатор чата */
    @Nonnull
    private long id;

    /** Идентификатор канала, которому принадлежит чат */
    @Nonnull
    @Column(name = "channel_id")
    private String channelId;
}
