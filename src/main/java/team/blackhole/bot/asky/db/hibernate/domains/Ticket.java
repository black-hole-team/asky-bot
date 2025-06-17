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
 * Домен обращения
 */
@Getter
@Setter
@Entity
@Table(
    name = "ticket",
    indexes = {
        @Index(name = "idx_ticket_status", columnList = "status"),
        @Index(name = "idx_ticket_chat_id", columnList = "chat_id")
    }
)
public class Ticket implements PersistentEntity {

    /** Идентификатор */
    @Id
    @Nonnull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Субъект обращения */
    @Nonnull
    @Column(length = 512)
    private String subject;

    /** Чат, в котором обращение было создано */
    @Nonnull
    @ManyToOne(fetch = FetchType.LAZY)
    private Chat chat;

    /** Список тем обращения */
    @Nonnull
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL)
    private List<HubTopic> topics;

    /** Статус обращения */
    @Nonnull
    @Enumerated(EnumType.STRING)
    private TicketStatus status;

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
