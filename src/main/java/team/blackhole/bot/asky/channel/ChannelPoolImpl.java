package team.blackhole.bot.asky.channel;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import team.blackhole.bot.asky.support.exception.AskyException;

import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Пул каналов
 */
@Log4j2
@RequiredArgsConstructor
public class ChannelPoolImpl implements ChannelPool {

    /** Каналы */
    private final Map<String, Channel> channels;

    /** Признак запущенного пула каналов */
    private boolean started;

    @Override
    public Channel getChannelById(String id) throws NoSuchElementException {
        var found = channels.get(id);
        if (found == null) {
            throw new NoSuchElementException();
        }
        return found;
    }

    @Override
    public void start() {
        if (started) {
            throw new AskyException("Пул каналов уже запущен");
        }
        var message = "запуска прослушивания каналов";
        try {
            log.info("Начало {}", message);
            for (var channel : channels.values()) {
                log.info("Запуск прослушивания канала '{}'", channel.getId());
                channel.start();
            }
            started = true;
            log.info("Конец {}", message);
        } catch (Exception e) {
            log.info("Ошибка {}", message, e);
        }
    }

    @Override
    public void close() {
        if (!started) {
            throw new AskyException("Пул каналов ещё не был запущен");
        }
        var message = "остановки прослушивания каналов";
        try {
            log.info("Начало {}", message);
            for (var channel : channels.values()) {
                log.info("Остановка прослушивания канала '{}'", channel.getId());
                channel.stop();
            }
            started = false;
            log.info("Конец {}", message);
        } catch (Exception e) {
            log.info("Ошибка {}", message, e);
        }
    }
}
