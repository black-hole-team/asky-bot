package team.blackhole.bot.asky.db.hibernate.repository;

import team.blackhole.bot.asky.db.hibernate.HibernateRepository;
import team.blackhole.bot.asky.db.hibernate.domains.Chat;
import team.blackhole.bot.asky.db.hibernate.domains.ChatId;

/**
 * Репозиторий для работы с чатами
 */
public interface ChatRepository extends HibernateRepository<Chat, ChatId> {
}
