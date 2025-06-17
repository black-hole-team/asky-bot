package team.blackhole.bot.asky.db.hibernate.domains;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import team.blackhole.bot.asky.db.hibernate.PersistentEntity;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;

/**
 * Домен чата поддержки на стороне пользователя
 */
@Getter
@Setter
@Entity
@Table(
    name = "chat",
    indexes = {
        @Index(name = "idx_chat_channel_chat_id_channel_id", columnList = "channel_chat_id,channel_id", unique = true)
    }
)
public class Chat implements PersistentEntity {

    /** Идентификатор */
    @Id
    @Nonnull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Идентификатор чата на стороне канала */
    @Nonnull
    @Column(name = "channel_chat_id")
    private String channelChatId;

    /** Идентификатор канала, которому принадлежит чат */
    @Nonnull
    @Column(name = "channel_id")
    private String channelId;

    /** Дата и время создания */
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /** Дата и время обновления */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /** Дата и время бана */
    @Column(name = "banned_at")
    private LocalDateTime bannedAt;

    @Override
    public boolean isNew() {
        return createdAt == null;
    }
}
