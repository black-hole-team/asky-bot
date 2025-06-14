package team.blackhole.bot.asky.db.hibernate.domains;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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
@Table(name = "chat")
public class Chat implements PersistentEntity {

    /** Идентификатор */
    @Nonnull
    @EmbeddedId
    private ChatId id;

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
