package team.blackhole.bot.asky.db.hibernate.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import team.blackhole.bot.asky.config.AskyDbConfiguration;

import javax.sql.DataSource;

/**
 * Поставщик источника данных
 */
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class DataSourceProvider implements Provider<DataSource> {

    /** Свойства базы данных */
    private final AskyDbConfiguration dbProperties;

    @Override
    public DataSource get() {
        var dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(dbProperties.getUrl());
        dataSource.setUsername(dbProperties.getUsername());
        dataSource.setPassword(dbProperties.getPassword());
        dataSource.setMaximumPoolSize(dbProperties.getMaxPoolSize());
        return dataSource;
    }
}
