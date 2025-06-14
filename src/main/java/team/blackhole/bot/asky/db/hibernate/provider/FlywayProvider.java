package team.blackhole.bot.asky.db.hibernate.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.flywaydb.core.Flyway;

import javax.sql.DataSource;

/**
 * Поставщик мигратора базы данных
 */
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class FlywayProvider implements Provider<Flyway> {

    /** Свойства мигратора базы данных */
    private final DataSource dataSource;

    @Override
    public Flyway get() {
        var flyway = Flyway.configure()
                .baselineOnMigrate(true)
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .load();
        // Запускаем миграции
        log.info("Запуск миграций базы данных");
        flyway.migrate();
        return flyway;
    }
}
