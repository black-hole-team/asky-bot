package team.blackhole.bot.asky.channel.capability;

import team.blackhole.bot.asky.channel.ChannelCapability;

/**
 * Возможность канала управления хабом
 */
public interface HubCapability extends ChannelCapability {

    /** Возвращает информацию о хабе по идентификатору */
    HubInfo getInfo(long hubId);

    /**
     * Создает тему в хабе
     * @param hubId идентификатор хаба
     * @param name  наименование темы
     */
    HubTopicInfo createHubTopic(long hubId, String name);

    /**
     * Информация о хабе
     */
    record HubInfo(String name) {

    }

    /**
     * Информация о теме хаба
     * @param id    идентификатор топика
     * @param hubId идентификатор хаба
     * @param name  наименование темы
     */
    record HubTopicInfo(long id, long hubId, String name) {

    }
}
