package team.blackhole.bot.asky.channel;

import java.io.InputStream;

/**
 * Вложение сообщения канала
 */
public interface ChannelAttachment {

    /**
     * Возвращает поток вывода вложения
     * @return поток вывода вложения
     */
    InputStream getInputStream();

    /**
     * Возвращает размер файла в байтах
     * @return размер файла в байтах
     */
    long getSize();

    /**
     * Возвращает идентификатор канала, которому принадлежит вложение
     * @return идентификатор канала, которому принадлежит вложение
     */
    String getChannelId();

    /**
     * Возвращает наименование вложения
     * @return наименование вложения
     */
    String getName();

    /**
     * Возвращает mime тип вложения
     * @return mime тип вложения
     */
    String getType();

    /**
     * Возвращает идентификатор вложения
     * @return идентификатор вложения
     */
    String getId();
}
