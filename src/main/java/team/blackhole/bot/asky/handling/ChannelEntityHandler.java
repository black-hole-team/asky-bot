package team.blackhole.bot.asky.handling;

import team.blackhole.bot.asky.channel.ChannelEntity;
import team.blackhole.bot.asky.db.jedis.domain.Stage;

/**
 * Обработчик сущностей канала
 * @param <T> тип обрабатываемой сущности
 */
public interface ChannelEntityHandler<T extends ChannelEntity> {

    /**
     * Обрабатывает получение новой сущности канала
     * @param stage  стадия обработки
     * @param entity сущность
     * @return новая стадия обработки
     */
    Stage handle(Stage stage, T entity);
}
