package team.blackhole.bot.asky.db.hibernate.domains;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import team.blackhole.bot.asky.db.hibernate.PersistentEntity;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * Домен темы в хабе обработки обращений
 */
@Getter
@Setter
@Entity
@Table(
    name = "hub_topic",
    indexes = {
        @Index(name = "idx_hub_topic_ticket_id", columnList = "ticket_id"),
        @Index(name = "idx_hub_topic_hub_id", columnList = "hub_id"),
        @Index(name = "idx_hub_topic_hub_topic_id", columnList = "hub_topic_id"),
        @Index(name = "idx_hub_topic_ticket_id_hub_id", columnList = "ticket_id,hub_id", unique = true),
        @Index(name = "idx_hub_topic_hub_topic_id_hub_id", columnList = "hub_topic_id,hub_id", unique = true),
    }
)
public class HubTopic implements PersistentEntity {

    /** Идентификатор */
    @Id
    @Nonnull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Идентификатор темы на стороне хаба */
    @Nonnull
    @Column(name = "hub_topic_id")
    private String hubTopicId;

    /** Хаб */
    @Nonnull
    @ManyToOne(fetch = FetchType.LAZY)
    private Hub hub;

    /** Обращение */
    @Nonnull
    @ManyToOne(fetch = FetchType.LAZY)
    private Ticket ticket;

    /** Дата и время создания */
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /** Дата и время обновления */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /** Дата и время, после наступления которой можно удалить эту тему */
    @Column(name = "delete_topic_after")
    private ZonedDateTime deleteTopicAfter;

    @Override
    public boolean isNew() {
        return createdAt == null;
    }
}
