package team.blackhole.bot.asky.db.jedis;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;
import redis.clients.jedis.JedisPool;
import team.blackhole.bot.asky.db.jedis.provider.JedisPoolProvider;
import team.blackhole.bot.asky.db.jedis.repository.StageRepository;
import team.blackhole.bot.asky.db.jedis.repository.StageRepositoryImpl;

/**
 * Модуль jedis контролируемой БД
 */
public class JedisModule implements Module {

    @Override
    public void configure(Binder binder) {
        // Пул jedis
        binder.bind(JedisPool.class).toProvider(JedisPoolProvider.class).in(Scopes.SINGLETON);

        // Репозиториии
        binder.bind(StageRepository.class).to(StageRepositoryImpl.class).in(Scopes.SINGLETON);
    }
}
