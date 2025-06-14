package team.blackhole.bot.asky.db;

import com.google.inject.Binder;
import com.google.inject.Module;
import team.blackhole.bot.asky.db.hibernate.HibernateModule;
import team.blackhole.bot.asky.db.jedis.JedisModule;

/**
 * Модуль базы данных
 */
public class DatabaseModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.install(new HibernateModule());
        binder.install(new JedisModule());
    }
}
