package team.blackhole.bot.asky.db.hibernate.domains;

/**
 * Тип хаба
 */
public enum HubType {

    /** Группа, в которой для каждого обращения создается отдельная тема */
    GROUP,

    /** Один чат, где на каждое открытое обращение нужно переключаться */
    SINGLE_CHAT
}
