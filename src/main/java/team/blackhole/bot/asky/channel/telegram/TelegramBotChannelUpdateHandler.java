package team.blackhole.bot.asky.channel.telegram;

import com.google.common.eventbus.EventBus;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.flywaydb.core.internal.util.UrlUtils;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.MaybeInaccessibleMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import team.blackhole.bot.asky.channel.*;
import team.blackhole.bot.asky.events.CallbackEvent;
import team.blackhole.bot.asky.events.MessageEvent;
import team.blackhole.bot.asky.support.exception.AskyException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
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
        var callbackQuery = update.getCallbackQuery();
        if (callbackQuery != null) {
            bus.post(getCallbackEvent(callbackQuery.getMessage(), callbackQuery, callbackQuery.getFrom()));
        }
        var message = update.getMessage();
        if (message != null) {
            bus.post(new MessageEvent(getChannelMessage(message, message.getFrom())));
        }
    }

    /**
     * Возвращает представление данных обратного вызова из telegram как общее представление данных канала
     * @param message       событие обратного вызова
     * @param callbackQuery запрос обратного вызова
     * @param from          отправитель
     * @return общее представление данных обратного вызова
     */
    @NotNull
    private CallbackEvent getCallbackEvent(MaybeInaccessibleMessage message, CallbackQuery callbackQuery, User from) {
        return new CallbackEvent(new ChannelCallback(message.getMessageId(), channelId, String.valueOf(message.getChatId()), callbackQuery.getData(),
                from.getId(), from.getLanguageCode() == null ? Locale.getDefault() : Locale.of(from.getLanguageCode())));
    }

    /**
     * Возвращает представление сообщения из telegram как общее сообщение канала
     * @param message сообщение telegram
     * @param from    отправитель сообщения
     * @return общее сообщение сообщения
     */
    private ChannelMessage getChannelMessage(Message message, User from) {
        return ChannelMessage.builder()
                .channelType(ChannelType.TELEGRAM_BOT)
                .channelId(channelId)
                .id(message.getMessageId())
                .chatId(String.valueOf(message.getChatId()))
                .topicId(message.getMessageThreadId() == null ? null : String.valueOf(message.getMessageThreadId()))
                .content(message.getText())
                .attachments(getAttachments(message))
                .userId(from.getId())
                .locale(from.getLanguageCode() == null ? Locale.getDefault() : Locale.forLanguageTag(from.getLanguageCode()))
                .source(message.getChatId() > 0 ? ChannelMessageSource.CHAT : ChannelMessageSource.GROUP)
                .build();
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
                return new URI(member.getFileUrl(botToken)).toURL().openStream();
            } catch (TelegramApiException | IOException | URISyntaxException e) {
                throw new AskyException("Ошибка при попытке получения информации о файле: [fileId = %s]".formatted(fileId), e);
            }
        }
    }
}
