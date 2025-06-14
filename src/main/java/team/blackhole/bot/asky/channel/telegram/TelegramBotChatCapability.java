package team.blackhole.bot.asky.channel.telegram;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import team.blackhole.bot.asky.channel.capability.ChatCapability;
import team.blackhole.bot.asky.support.exception.AskyException;

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
        if (sending.content() != null) {
            sendContent(sending);
        }
        if (sending.attachments() != null) {
            sendAttachments(sending);
        }
    }

    @Override
    public ChatUserInfo getChatUserInfo(long chatId, long userId) {
        try {
            var member = client.execute(new GetChatMember(String.valueOf(chatId), userId));
            var user = member.getUser();
            return new ChatUserInfo(userId, user.getUserName(), user.getFirstName(), user.getLastName());
        } catch (TelegramApiException e) {
            throw new AskyException("Ошибка при попытке получить пользователя чата: [chatId = %s, userId = %s]".formatted(chatId, userId), e);
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
                sendDocumentBuilder.messageThreadId(Math.toIntExact(sending.topicId()));
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
            builder.messageThreadId(Math.toIntExact(sending.topicId()));
        }
        try {
            client.execute(builder.build());
        } catch (TelegramApiException e) {
            throw new AskyException("Ошибка при отправке сообщения: %s".formatted(sending), e);
        }
    }
}
