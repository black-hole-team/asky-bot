package team.blackhole.bot.asky.channel.capability;

import team.blackhole.bot.asky.channel.ChannelCapability;

/**
 * Возможность канала управления хабом
 */
public interface HubCapability extends ChannelCapability {

    /** Возвращает информацию о хабе по идентификатору */
    HubInfo getInfo(String hubId);

    /**
     * Создает тему в хабе
     * @param hubId идентификатор хаба
     * @param name  наименование темы
     */
    HubTopicInfo createHubTopic(String hubId, String name);

    /**
     * Удаляет тему хаба
     * @param hubId   идентификатор хаба
     * @param topicId идентификатор темы
     */
    void deleteHubTopic(String hubId, String topicId);

    /**
     * Информация о хабе
     */
    record HubInfo(String name) {

    }

    /**
     * Информация о теме хаба
     * @param hubTopicId идентификатор темы в хабе
     * @param hubId      идентификатор хаба
     * @param name       наименование темы
     */
    record HubTopicInfo(String hubTopicId, String hubId, String name) {

    }
}
