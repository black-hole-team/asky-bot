package team.blackhole.bot.asky.queue.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import redis.clients.jedis.JedisPool;
import team.blackhole.bot.asky.queue.RedisDelayedQueue;

/**
 * Очередь закрытых обращений на удаление темы
 */
public class TicketDelayedQueue extends RedisDelayedQueue<Long> {

    /** Ключ очереди закрытых обращений на удаление темы */
    private static final String TICKET_DELAYED_QUEUE_KEY = "ticket:queue";

    /** Маппер объектов */
    private final ObjectMapper objectMapper;

    /** Пул jedis */
    private final JedisPool jedisPool;

    /**
     * Конструктор
     * @param objectMapper маппер объектов
     * @param jedisPool    пул jedis
     */
    @Inject
    public TicketDelayedQueue(ObjectMapper objectMapper, JedisPool jedisPool) {
        super(TICKET_DELAYED_QUEUE_KEY);
        this.objectMapper = objectMapper;
        this.jedisPool = jedisPool;
    }

    @Override
    protected Class<Long> getEntityClass() {
        return Long.class;
    }

    @Override
    protected ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    @Override
    protected JedisPool getJedisPool() {
        return jedisPool;
    }
}
