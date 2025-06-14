package team.blackhole.bot.asky.db.hibernate.domains;

/**
 * Статус обращения
 */
public enum TicketStatus {

    /** Обращение открыто */
    OPEN,

    /** Ожидает ответа */
    AWAITING_RESPONSE,

    /** Отвечено */
    ANSWERED,

    /** Обращение решено */
    RESOLVED
}
