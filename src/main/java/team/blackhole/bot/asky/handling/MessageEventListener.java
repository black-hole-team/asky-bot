package team.blackhole.bot.asky.handling;

import com.google.common.eventbus.Subscribe;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import team.blackhole.bot.asky.db.hibernate.HibernateSessionContextUtils;
import team.blackhole.bot.asky.handling.stage.StageManager;
import team.blackhole.bot.asky.handling.events.MessageEvent;

import java.util.concurrent.ExecutorService;

/**
 * Слушатель получения нового сообщения
 */
@Log4j2
@RequiredArgsConstructor
public class MessageEventListener {

    /** Менеджер стадий */
    private final StageManager stageManager;

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
            try {
                stageManager.process(event);
            } catch (Throwable e) {
                log.error("Ошибка при обработке события получения сообщения", e);
            } finally {
                HibernateSessionContextUtils.unbind((SessionFactoryImplementor) sessionFactory);
            }
        });
    }
}
