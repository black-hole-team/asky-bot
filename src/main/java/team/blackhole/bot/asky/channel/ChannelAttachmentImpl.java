package team.blackhole.bot.asky.channel;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.InputStream;
import java.util.function.Supplier;

/**
 * Вложение бота телеграм
 */
@Getter
@RequiredArgsConstructor
public class ChannelAttachmentImpl implements ChannelAttachment {

    /** Клиент telegram */
    private final Supplier<InputStream> inputStreamSupplier;

    /** Размер файла в байтах */
    private final long size;

    /** Идентификатор канала, которому принадлежит вложение */
    private final String channelId;

    /** Идентификатор файла вложения */
    private final String id;

    /** Наименование вложения */
    private final String name;

    /** Тип вложения */
    private final String type;

    @Override
    public InputStream getInputStream() {
        return inputStreamSupplier.get();
    }
}
