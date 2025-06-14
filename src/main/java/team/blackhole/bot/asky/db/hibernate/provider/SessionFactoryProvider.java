package team.blackhole.bot.asky.db.hibernate.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.tool.schema.Action;
import team.blackhole.bot.asky.config.AskyDbConfiguration;
import team.blackhole.bot.asky.db.hibernate.HibernateSessionContext;
import team.blackhole.bot.asky.db.hibernate.domains.Chat;
import team.blackhole.bot.asky.db.hibernate.domains.Hub;
import team.blackhole.bot.asky.db.hibernate.domains.HubTopic;
import team.blackhole.bot.asky.db.hibernate.domains.Ticket;

import javax.sql.DataSource;

/**
 * Поставщик фабрики сессий
 */
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class SessionFactoryProvider implements Provider<SessionFactory> {

    /** Свойства базы данных */
    private final AskyDbConfiguration dbProperties;

    /** Источник данных */
    private final DataSource dataSource;

    @Override
    public SessionFactory get() {
        return sources()
                .getMetadataBuilder()
                .build()
                .getSessionFactoryBuilder()
                .build();
    }

    /**
     * Возвращает источник метаданных
     * @return источник метаданных
     */
    private MetadataSources sources() {
        return new MetadataSources(serviceRegistry())
                .addAnnotatedClass(Chat.class)
                .addAnnotatedClass(Hub.class)
                .addAnnotatedClass(Ticket.class)
                .addAnnotatedClass(HubTopic.class);
    }

    /**
     * Возвращает регистр сервисов
     * @return регистр сервисов
     */
    private StandardServiceRegistry serviceRegistry() {
        return new StandardServiceRegistryBuilder()
                .applySetting(AvailableSettings.JAKARTA_NON_JTA_DATASOURCE, dataSource)
                .applySetting(AvailableSettings.JAKARTA_JDBC_DRIVER, dbProperties.getDriver())
                .applySetting(AvailableSettings.STATEMENT_BATCH_SIZE, dbProperties.getBatchSize())
                .applySetting(AvailableSettings.CURRENT_SESSION_CONTEXT_CLASS, HibernateSessionContext.class.getName())
                .applySetting(AvailableSettings.HBM2DDL_AUTO, Action.ACTION_VALIDATE)
                .build();
    }
}
