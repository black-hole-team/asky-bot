package team.blackhole.bot.asky.db.hibernate;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.matcher.Matchers;
import org.flywaydb.core.Flyway;
import org.hibernate.SessionFactory;
import team.blackhole.bot.asky.db.hibernate.provider.DataSourceProvider;
import team.blackhole.bot.asky.db.hibernate.provider.FlywayProvider;
import team.blackhole.bot.asky.db.hibernate.provider.SessionFactoryProvider;
import team.blackhole.bot.asky.db.hibernate.repository.*;

import javax.sql.DataSource;

/**
 * Модуль hibernate контролируемой БД
 */
public class HibernateModule implements Module {

    @Override
    public void configure(Binder binder) {
        // Сервисные классы
        binder.bind(DataSource.class).toProvider(DataSourceProvider.class).in(Scopes.SINGLETON);
        binder.bind(Flyway.class).toProvider(FlywayProvider.class).in(Scopes.SINGLETON);
        binder.bind(SessionFactory.class).toProvider(SessionFactoryProvider.class).in(Scopes.SINGLETON);
        binder.bindListener(Matchers.any(), new HibernateTransactionalListener(binder.getProvider(SessionFactory.class)));

        // Репозитории
        binder.bind(HubRepository.class).to(HubRepositoryImpl.class).in(Scopes.SINGLETON);
        binder.bind(TicketRepository.class).to(TicketRepositoryImpl.class).in(Scopes.SINGLETON);
        binder.bind(ChatRepository.class).to(ChatRepositoryImpl.class).in(Scopes.SINGLETON);
    }
}
