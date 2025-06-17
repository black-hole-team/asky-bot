package team.blackhole.bot.asky.service.ticket;

import team.blackhole.bot.asky.db.hibernate.domains.HubTopic;
import team.blackhole.bot.asky.db.hibernate.domains.Ticket;

/**
 * Сервис для работы с обращениями после их решения
 */
public interface TicketAfterResolveService {

    /**
     * Удаляет топик заявки после её закрытия
     * @param ticketId идентификатор заявки
     */
    Ticket deleteTicketTopics(Ticket ticketId);

    /**
     * Продлевает время, через которое тема будет удалена
     * @param topicId идентификатор темы
     */
    HubTopic continuationTopic(long topicId);

    /**
     * Удаляет топики уже истекших обращений
     */
    void doDeleteTopics();
}
