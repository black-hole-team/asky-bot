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
@Table(
    name = "hub",
    indexes = {
        @Index(name = "idx_hub_channel_hub_id_channel_id", columnList = "channel_hub_id,channel_id", unique = true)
    }
)
public class Hub implements PersistentEntity {

    /** Идентификатор */
    @Id
    @Nonnull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Идентификатор хаба на стороне канала */
    @Nonnull
    @Column(name = "channel_hub_id")
    private String channelHubId;

    /** Идентификатор канала, на котором работает хаб */
    @Nonnull
    @Column(name = "channel_id")
    private String channelId;

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
