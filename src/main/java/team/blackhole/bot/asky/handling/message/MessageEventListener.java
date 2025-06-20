package team.blackhole.bot.asky.handling.message;

import com.google.common.eventbus.Subscribe;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import team.blackhole.bot.asky.channel.sending.MessageSenderImpl;
import team.blackhole.bot.asky.db.hibernate.HibernateSessionContextUtils;
import team.blackhole.bot.asky.db.jedis.domain.Stage;
import team.blackhole.bot.asky.db.jedis.repository.StageRepository;
import team.blackhole.bot.asky.handling.events.MessageEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * Обработчик событий новых сообщения
 */
@Log4j2
@RequiredArgsConstructor
public class MessageEventListener {

    /** Отсутствующая стадия */
    public static final Stage NONE = new Stage(StageName.NONE, new HashMap<>(), null, false);

    /** Карта, где ключ, это наименование стадии, а значение это сама стадия */
    private final Map<StageName, MessageHandler> stages = new HashMap<>();

    /** Репозитория для работы со стадией */
    private final StageRepository stageRepository;

    /** Сервис исполнитель обработки сообщений */
    private final ExecutorService executor;

    /** Фабрика сессий hibernate */
    private final SessionFactory sessionFactory;

    /**
     * Обрабатывает получение нового сообщения
     * @param event событие получения нового сообщения
     */
    @Subscribe
    public void handle(MessageEvent event) {
        if (event.isCanceled()) {
            return;
        }
        executor.submit(() -> {
            // Устанавливаем локаль для отправки сообщений
            MessageSenderImpl.SENDING_LOCALE.set(event.getMessage().locale());
            // Обрабатываем сообщение
            try {
                process(event);
            } catch (Throwable e) {
                log.error("Ошибка при обработке события получения сообщения", e);
            } finally {
                HibernateSessionContextUtils.unbind((SessionFactoryImplementor) sessionFactory);
            }
        });
    }

    /**
     * Регистрирует новый обработчик сообщений
     * @param name    наименование стадии
     * @param handler обработчик сообщений
     */
    public void register(StageName name, MessageHandler handler) {
        stages.put(name, handler);
    }

    /**
     * Обрабатывает поступление нового сообщения
     * @param event событие сообщения
     */
    public void process(MessageEvent event) {
        var message = event.getMessage();
        var stage = stageRepository.findByChannelTypeAndUserIdAndChatId(message.channelId(), message.chatId())
                .orElse(NONE);
        var stageFirst = stage;
        var prevStageName = stage.name();
        // Обрабатываем сообщение
        do {
            var handler = stages.get(stage.name());
            if (handler == null) {
                continue;
            }
            prevStageName = stage.name();
            stage = handler.handle(stage, message);
        } while (stage.propagation() && prevStageName != stage.name());
        // Если стадия изменилась, обновляем её в репозитории
        if (!stageFirst.equals(stage)) {
            log.info("Обновление стадии {} -> {}", stageFirst, stage);
            stageRepository.updateByChannelTypeAndUserIdAndChatId(message.channelId(), message.chatId(), stage);
        }
    }
}
