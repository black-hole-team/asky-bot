package team.blackhole.bot.asky.channel.telegram;

import com.google.common.eventbus.EventBus;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import team.blackhole.bot.asky.channel.ChannelAttachment;
import team.blackhole.bot.asky.channel.ChannelMessage;
import team.blackhole.bot.asky.channel.ChannelType;
import team.blackhole.bot.asky.channel.ChannelAttachmentImpl;
import team.blackhole.bot.asky.handling.events.MessageEvent;
import team.blackhole.bot.asky.support.exception.AskyException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.function.Supplier;

/**
 * Обработчик обновлений telegram бота
 */
@Log4j2
@RequiredArgsConstructor
public class TelegramBotChannelUpdateHandler implements LongPollingSingleThreadUpdateConsumer {

    /** Клиент телеграм */
    private final TelegramClient client;

    /** Шина событий */
    private final EventBus bus;

    /** Идентификатор канала */
    private final String channelId;

    /** Токен бота */
    private final String botToken;

    @Override
    public void consume(Update update) {
        var message = update.getMessage();
        if (message != null) {
            var from = message.getFrom();
            bus.post(new MessageEvent(ChannelMessage.builder()
                            .channelType(ChannelType.TELEGRAM_BOT)
                            .channelId(channelId)
                            .id(message.getMessageId())
                            .chatId(message.getChatId())
                            .topicId(message.getMessageThreadId() == null ? null : Long.valueOf(message.getMessageThreadId()))
                            .userId(from.getId())
                            .locale(from.getLanguageCode() == null ? Locale.getDefault() : new Locale(from.getLanguageCode()))
                            .content(message.getText())
                            .attachments(getAttachments(message))
                            .build()));
        }
    }

    /**
     * Возвращает вложения сообщения
     * @param message сообщение
     * @return вложения сообщения
     */
    private List<ChannelAttachment> getAttachments(Message message) {
        var attachments = new ArrayList<ChannelAttachment>();
        var document = message.getDocument();
        if (document != null) {
            attachments.add(new ChannelAttachmentImpl(new AttachmentStreamSupplier(document.getFileId()), document.getFileSize(), channelId,
                document.getFileId(), document.getFileName(), document.getMimeType()));
        }
        var photo = message.getPhoto();
        if (photo != null) {
            photo.stream().min(Comparator.comparingInt(e -> -e.getFileSize()))
                .ifPresent(photoSize -> attachments.add(new ChannelAttachmentImpl(new AttachmentStreamSupplier(photoSize.getFileId()), photoSize.getFileSize(),
                        channelId, photoSize.getFileId(), photoSize.getFileUniqueId(), "image/jpeg")));
        }
        return Collections.unmodifiableList(attachments);
    }

    /**
     * Получатель потока на чтение файла
     */
    @RequiredArgsConstructor
    public class AttachmentStreamSupplier implements Supplier<InputStream> {

        /** Идентификатор файла для получения его содержимого */
        private final String fileId;

        @Override
        public InputStream get() {
            try {
                var member = client.execute(new GetFile(fileId));
                return new URL(member.getFileUrl(botToken)).openStream();
            } catch (TelegramApiException | IOException e) {
                throw new AskyException("Ошибка при попытке получения информации о файле: [fileId = %s]".formatted(fileId), e);
            }
        }
    }
}
