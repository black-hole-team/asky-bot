package team.blackhole.bot.asky.channel.telegram;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.internal.util.collections.CollectionHelper;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import team.blackhole.bot.asky.channel.capability.ChatCapability;
import team.blackhole.bot.asky.support.exception.AskyException;

import java.util.ArrayList;

/**
 * Возможность отправки сообщений через telegram бота
 */
@Log4j2
@RequiredArgsConstructor
public class TelegramBotChatCapability implements ChatCapability {

    /** Клиент telegram */
    private final TelegramClient client;

    /** Максимальный размер файла */
    private final long maxFileSize;

    @Override
    public void send(MessageSending sending) {
        if (!StringUtils.isEmpty(sending.content())) {
            sendContent(sending);
        }
        if (!CollectionHelper.isEmpty(sending.attachments())) {
            sendAttachments(sending);
        }
    }

    @Override
    public void edit(MessageEdit edit) {
        if (StringUtils.isEmpty(edit.content())) {
            if (!CollectionHelper.isEmpty(edit.actions())) {
                sendReplyMarkup(edit);
            }
        } else {
            sendContent(edit);
        }
    }

    @Override
    public ChatUserInfo getChatUserInfo(String chatId, long userId) {
        try {
            var member = client.execute(new GetChatMember(chatId, userId));
            var user = member.getUser();
            return new ChatUserInfo(userId, user.getUserName(), user.getFirstName(), user.getLastName());
        } catch (TelegramApiException e) {
            throw new AskyException("Ошибка при попытке получить пользователя чата: [chatId = %s, userId = %s]".formatted(chatId, userId), e);
        }
    }

    /**
     * Отправляет отредактированную разметку сообщения
     * @param edit данные для редактирования сообщения
     */
    private void sendReplyMarkup(MessageEdit edit) {
        var builder = EditMessageReplyMarkup.builder()
                .chatId(edit.chatId())
                .messageId(edit.messageId());
        var rows = new ArrayList<InlineKeyboardRow>();
        for (var buttons : edit.actions()) {
            var row = new InlineKeyboardRow();
            for (var button : buttons) {
                row.add(InlineKeyboardButton.builder()
                        .text(button.text())
                        .callbackData(button.payload())
                        .build());
            }
            rows.add(row);
        }
        builder.replyMarkup(new InlineKeyboardMarkup(rows));
        try {
            client.execute(builder.build());
        } catch (TelegramApiException e) {
            throw new AskyException("Ошибка при отправке сообщения: %s".formatted(edit), e);
        }
    }

    /**
     * Отправляет отредактированное содержимое сообщения
     * @param edit данные для редактирования сообщения
     */
    private void sendContent(MessageEdit edit) {
        var builder = EditMessageText.builder()
                .chatId(edit.chatId())
                .messageId(edit.messageId())
                .text(edit.content());
        if (!CollectionHelper.isEmpty(edit.actions())) {
            var rows = new ArrayList<InlineKeyboardRow>();
            for (var buttons : edit.actions()) {
                var row = new InlineKeyboardRow();
                for (var button : buttons) {
                    row.add(InlineKeyboardButton.builder()
                            .text(button.text())
                            .callbackData(button.payload())
                            .build());
                }
                rows.add(row);
            }
            builder.replyMarkup(new InlineKeyboardMarkup(rows));
        }
        try {
            client.execute(builder.build());
        } catch (TelegramApiException e) {
            throw new AskyException("Ошибка при отправке сообщения: %s".formatted(edit), e);
        }
    }

    /**
     * Отправляет вложения сообщения в чат
     * @param sending отправление с вложениями
     */
    private void sendAttachments(MessageSending sending) {
        for (var attachment : sending.attachments()) {
            if (attachment.getSize() > maxFileSize) {
                log.warn("Размер файла '{}' превышает максимально допустимый для отправки размер файла в {} [size = {}]", attachment.getId(),
                        maxFileSize, attachment.getSize());
                continue;
            }
            var sendDocumentBuilder = SendDocument.builder()
                    .chatId(sending.chatId())
                    .document(new InputFile(attachment.getInputStream(), attachment.getName()));
            if (sending.replyTo() != null) {
                sendDocumentBuilder.replyToMessageId(Math.toIntExact(sending.replyTo()));
            }
            if (sending.topicId() != null) {
                sendDocumentBuilder.messageThreadId(Integer.parseInt(sending.topicId()));
            }
            try {
                client.execute(sendDocumentBuilder.build());
            } catch (TelegramApiException e) {
                throw new AskyException("Ошибка при отправке документа: %s".formatted(attachment), e);
            }
        }
    }

    /**
     * Отправляет текстовое сообщение в чат
     * @param sending отправление
     */
    private void sendContent(MessageSending sending) {
        var builder = SendMessage.builder()
                .chatId(sending.chatId())
                .text(sending.content());
        if (sending.replyTo() != null) {
            builder.replyToMessageId(Math.toIntExact(sending.replyTo()));
        }
        if (sending.topicId() != null) {
            builder.messageThreadId(Integer.parseInt(sending.topicId()));
        }
        if (!CollectionHelper.isEmpty(sending.actions())) {
            var rows = new ArrayList<InlineKeyboardRow>();
            for (var buttons : sending.actions()) {
                var row = new InlineKeyboardRow();
                for (var button : buttons) {
                    row.add(InlineKeyboardButton.builder()
                            .text(button.text())
                            .callbackData(button.payload())
                            .build());
                }
                rows.add(row);
            }
            builder.replyMarkup(new InlineKeyboardMarkup(rows));
        }
        try {
            client.execute(builder.build());
        } catch (TelegramApiException e) {
            throw new AskyException("Ошибка при отправке сообщения: %s".formatted(sending), e);
        }
    }
}
