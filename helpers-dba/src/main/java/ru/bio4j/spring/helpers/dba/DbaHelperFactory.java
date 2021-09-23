package ru.bio4j.spring.helpers.dba;

import ru.bio4j.spring.commons.types.ApplicationContextProvider;
import ru.bio4j.spring.commons.types.ExcelBuilder;
import ru.bio4j.spring.commons.utils.Utl;
import ru.bio4j.spring.database.api.SQLContext;
import ru.bio4j.spring.database.commons.DbContextAbstract;
import ru.bio4j.spring.database.commons.DbContextFactory;
import ru.bio4j.spring.model.config.props.DataSourceProperties;

/**
 * Фабрика для создания DbaHelper
 */
public class DbaHelperFactory {

    private final ExcelBuilder excelBuilder;

    public DbaHelperFactory(ExcelBuilder excelBuilder) {
        this.excelBuilder = excelBuilder;
    }

    public DbaHelper create(DataSourceProperties dataSourceProperties) {
        try {
            Class<? extends DbContextAbstract> sqlContextType =  DBMSType.getSqlContextTypeByName(dataSourceProperties.getDbmsName());
            if (sqlContextType == null)
                throw new IllegalArgumentException(String.format("Unknown dbmsName (%s) in dataSourceProperties!", dataSourceProperties.getDbmsName()));
            final SQLContext sqlContext = DbContextFactory.createHikariCP(dataSourceProperties, sqlContextType);
            return new DbaHelper(sqlContext, excelBuilder);
        } catch (Exception e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    public DbaHelper create(String dataSourceName) {
        final DataSourceProperties dataSourceProperties = (DataSourceProperties) ApplicationContextProvider.getApplicationContext().getBean(dataSourceName);
        return create(dataSourceProperties);
    }
}
