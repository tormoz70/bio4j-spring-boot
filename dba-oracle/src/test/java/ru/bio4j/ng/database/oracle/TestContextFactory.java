package ru.bio4j.ng.database.oracle;

import com.zaxxer.hikari.HikariConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.converter.ConvertValueException;
import ru.bio4j.ng.commons.converter.Converter;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.database.api.SQLConnectionPoolConfig;
import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.database.api.SQLContextConfig;
import ru.bio4j.ng.database.commons.DbContextAbstract;
import ru.bio4j.ng.model.transport.BioError;

import javax.sql.DataSource;
import java.lang.reflect.Constructor;
import java.util.Properties;

public class TestContextFactory {
    private static final Logger LOG = LoggerFactory.getLogger(TestContextFactory.class);

    private static <T> T getValFromCfg(String str, final String defaultStr, final Class<T> type) throws ConvertValueException {
        if(Strings.isNullOrEmpty(str)) str = defaultStr;
        return Converter.toType(str, type);
    }

    public static <T extends DbContextAbstract> SQLContext createHikariCP(SQLConnectionPoolConfig config, Class<T> clazz) {
        if(LOG.isDebugEnabled())
            LOG.debug("Creating SQLContext with:\n" + Utl.buildBeanStateInfo(config, null, "\t"));
        final Properties properties = new Properties();
        properties.setProperty("dataSource.cachePrepStmts", "true");
        properties.setProperty("dataSource.prepStmtCacheSize", "250");
        properties.setProperty("dataSource.prepStmtCacheSqlLimit", "2048");

        HikariConfig cfg = new HikariConfig();
        cfg.setPoolName(config.getPoolName());
        cfg.setAutoCommit(false);
        cfg.setDriverClassName(config.getDbDriverName());
        cfg.setJdbcUrl(config.getDbConnectionUrl());
        cfg.setUsername(config.getDbConnectionUsr());
        cfg.setPassword(config.getDbConnectionPwd());
        cfg.setMaximumPoolSize(getValFromCfg(config.getMaxPoolSize(), "10", int.class));
        cfg.setMinimumIdle(getValFromCfg(config.getMinIdle(), "2", int.class));
        cfg.setDataSourceProperties(properties);
        DataSource dataSource = new com.zaxxer.hikari.HikariDataSource(cfg);

        SQLContextConfig sqlContextConfig = new SQLContextConfig();
        sqlContextConfig.setCurrentSchema(config.getCurrentSchema());

        try {
            Constructor<T> constructor = clazz.getConstructor(DataSource.class, SQLContextConfig.class);
            return constructor.newInstance(new Object[]{dataSource, sqlContextConfig});
        } catch (Exception e) {
            throw BioError.wrap(e);
        }
    }

}
