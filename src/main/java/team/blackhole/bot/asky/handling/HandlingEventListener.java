package team.blackhole.bot.asky.handling;

import com.google.common.eventbus.Subscribe;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import team.blackhole.bot.asky.channel.ChannelEntity;
import team.blackhole.bot.asky.channel.sending.MessageSenderImpl;
import team.blackhole.bot.asky.db.hibernate.HibernateSessionContextUtils;
import team.blackhole.bot.asky.db.jedis.domain.Stage;
import team.blackhole.bot.asky.db.jedis.repository.StageRepository;
import team.blackhole.bot.asky.events.CallbackEvent;
import team.blackhole.bot.asky.events.MessageEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * Обработчик событий канала
 */
@Log4j2
@RequiredArgsConstructor
public class HandlingEventListener {

    /** Отсутствующая стадия */
    public static final Stage NONE = new Stage(StageName.NONE, new HashMap<>(), null, false);

    /** Карта, где ключ, это наименование стадии, а значение это обработчик сообщения на этой стадии */
    private final Map<Class<? extends ChannelEntity>, Map<StageName, ChannelEntityHandler<?>>> entityTypeToHandler = new HashMap<>();

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
        submit(event.getMessage());
    }

    /**
     * Обрабатывает получение данных обратного вызова
     * @param event событие получения данных обратного вызова
     */
    @Subscribe
    public void handle(CallbackEvent event) {
        if (event.isCanceled()) {
            return;
        }
        submit(event.getCallback());
    }

    /**
     * Регистрирует слушатель
     * @param stage   стадия обработки
     * @param type    тип сущности
     * @param handler обработчик
     * @param <T>     тип обработчика
     * @param <E>     тип сущности
     */
    public <T extends ChannelEntityHandler<E>, E extends ChannelEntity> void register(StageName stage, Class<E> type, T handler) {
        this.entityTypeToHandler.computeIfAbsent(type, key -> new HashMap<>())
                .put(stage, handler);
    }

    /**
     * Обрабатывает получение новой сущности канала
     * @param entity сущность
     */
    private void submit(ChannelEntity entity) {
        executor.submit(() -> {
            // Устанавливаем локаль для отправки сообщений
            MessageSenderImpl.SENDING_LOCALE.set(entity.locale());
            // Обрабатываем сообщение
            try {
                process(entity);
            } catch (Throwable e) {
                log.error("Ошибка при обработке события получения сообщения", e);
            } finally {
                HibernateSessionContextUtils.unbind((SessionFactoryImplementor) sessionFactory);
            }
        });
    }

    /**
     * Обрабатывает сущность канала
     * @param entity сущность
     */
    private void process(ChannelEntity entity) {
        final var initialStage = stageRepository.findByChannelTypeAndUserIdAndChatId(entity.channelId(), entity.chatId())
                .orElse(NONE);

        var stage = initialStage;
        var stageNameToHandler = entityTypeToHandler.getOrDefault(entity.getClass(), Collections.emptyMap());
        var prevStageName = stage.name();

        // Обрабатываем сообщение
        do {
            var handler = stageNameToHandler.get(stage.name());

            if (handler == null) {
                continue;
            }

            prevStageName = stage.name();
            stage = getHandle(entity, handler, stage);
        } while (stage.propagation() && prevStageName != stage.name());

        // Если стадия изменилась, обновляем её в репозитории
        if (!initialStage.equals(stage)) {
            stageRepository.updateByChannelTypeAndUserIdAndChatId(entity.channelId(), entity.chatId(), stage);
        }
    }

    /**
     * Возвращает результат обработки сущности канала
     * @param entity  сущность канала
     * @param handler обработчик
     * @param stage   стадия обработки
     * @return результат обработки
     * @param <T> тип сущности
     */
    @SuppressWarnings("unchecked")
    private static <T extends ChannelEntity> Stage getHandle(ChannelEntity entity, ChannelEntityHandler<T> handler, Stage stage) {
        return handler.handle(stage, (T) entity);
    }
}
