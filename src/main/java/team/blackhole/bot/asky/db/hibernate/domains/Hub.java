package team.blackhole.bot.asky.db.hibernate.domains;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import team.blackhole.bot.asky.db.hibernate.PersistentEntity;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Домен хаба (места обработки сообщений)
 */
@Getter
@Setter
@Entity
@Table(name = "hub")
public class Hub implements PersistentEntity {

    /** Идентификатор */
    @Nonnull
    @EmbeddedId
    private HubId id;

    /** Наименование хаба */
    @Nonnull
    private String name;

    /** Список тем хаба */
    @Nonnull
    @OneToMany(mappedBy = "hub", fetch = FetchType.LAZY)
    private List<HubTopic> topics;

    /** Дата и время создания */
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /** Дата и время обновления */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Override
    public boolean isNew() {
        return createdAt == null;
    }
}
