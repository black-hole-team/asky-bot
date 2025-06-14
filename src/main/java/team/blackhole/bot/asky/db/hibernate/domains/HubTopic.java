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
 * Домен темы в хабе обработки обращений
 */
@Getter
@Setter
@Entity
@Table(
    name = "hub_topic",
    indexes = {
        @Index(name = "hub_topic_ticket_id", columnList = "ticket_id"),
        @Index(name = "hub_topic_hub_id", columnList = "hub_id"),
        @Index(name = "hub_topic_hub_channel_id", columnList = "hub_channel_id"),
    }
)
public class HubTopic implements PersistentEntity {

    /** Идентификатор темы */
    @Id
    @Nonnull
    private long id;

    /** Хаб */
    @Nonnull
    @ManyToOne(fetch = FetchType.LAZY)
    private Hub hub;

    /** Идентификатор хаба */
    @Nonnull
    @Column(name = "hub_id", insertable = false, updatable = false)
    private long hubId;

    /** Идентификатор канала хаба */
    @Nonnull
    @Column(name = "hub_channel_id", insertable = false, updatable = false)
    private String hubChannelId;

    /** Обращение */
    @Nonnull
    @ManyToOne(fetch = FetchType.LAZY)
    private Ticket ticket;

    /** Идентификатор обращения */
    @Nonnull
    @Column(name = "ticket_id", insertable = false, updatable = false)
    private long ticketId;

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

    /**
     * Устанавливает хаб
     * @param hub хаб
     */
    public void setHub(@Nonnull Hub hub) {
        this.hub = hub;
        this.hubId = hub.getId().getId();
        this.hubChannelId = hub.getId().getChannelId();
    }
}
