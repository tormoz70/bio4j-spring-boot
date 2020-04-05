package ru.bio4j.spring.database.commons;

import com.zaxxer.hikari.HikariConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.spring.model.transport.ConvertValueException;
import ru.bio4j.spring.commons.converter.Converter;
import ru.bio4j.spring.commons.utils.Strings;
import ru.bio4j.spring.commons.utils.Utl;
import ru.bio4j.spring.model.transport.DataSourceProperties;
import ru.bio4j.spring.database.api.SQLContext;
import ru.bio4j.spring.model.transport.BioError;

import javax.sql.DataSource;
import java.lang.reflect.Constructor;
import java.util.Properties;

public class DbContextFactory {
    private static final Logger LOG = LoggerFactory.getLogger(DbContextFactory.class);

    private static <T> T getValFromCfg(String str, final String defaultStr, final Class<T> type) throws ConvertValueException {
        if(Strings.isNullOrEmpty(str))
            str = defaultStr;
        return Converter.toType(str, type);
    }

    public static <T extends DbContextAbstract> SQLContext createHikariCP(DataSourceProperties dataSourceProperties, Class<T> clazz) {
        if(LOG.isDebugEnabled())
            LOG.debug("Creating SQLContext with:\n" + Utl.buildBeanStateInfo(dataSourceProperties, null, "\t"));
        final Properties properties = new Properties();
        properties.setProperty("dataSource.cachePrepStmts", "true");
        properties.setProperty("dataSource.prepStmtCacheSize", "250");
        properties.setProperty("dataSource.prepStmtCacheSqlLimit", "2048");

        HikariConfig cfg = new HikariConfig();
        cfg.setAutoCommit(false);
        cfg.setDriverClassName(dataSourceProperties.getDriverClassName());
        cfg.setJdbcUrl(dataSourceProperties.getUrl());
        cfg.setUsername(dataSourceProperties.getUsername());
        cfg.setPassword(dataSourceProperties.getPassword());
        cfg.setMaximumPoolSize(getValFromCfg(dataSourceProperties.getMaximumPoolSize(), "10", int.class));
        cfg.setMinimumIdle(getValFromCfg(dataSourceProperties.getMinimumPoolSize(), "2", int.class));
        cfg.setDataSourceProperties(properties);

        DataSource dataSource = new com.zaxxer.hikari.HikariDataSource(cfg);
        try {
            Constructor<T> constructor = clazz.getConstructor(DataSource.class, DataSourceProperties.class);
            return constructor.newInstance(new Object[]{dataSource, dataSourceProperties});
        } catch (Exception e) {
            throw BioError.wrap(e);
        }
    }

}
