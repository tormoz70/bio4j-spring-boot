package ru.bio4j.spring.database.oracle;

import ru.bio4j.spring.commons.types.LogWrapper;
import ru.bio4j.spring.commons.utils.Strings;
import ru.bio4j.spring.database.api.SQLReader;
import ru.bio4j.spring.database.commons.DbContextAbstract;
import ru.bio4j.spring.database.commons.DbUtils;
import ru.bio4j.spring.model.BaseDataSourceProperties;

import javax.sql.DataSource;

public class OracleContext extends DbContextAbstract {
    private static final LogWrapper LOG = LogWrapper.getLogger(OracleContext.class);

    public OracleContext(final DataSource dataSource, final BaseDataSourceProperties dataSourceProperties) {
        super(dataSource, dataSourceProperties);

        if(this.getDataSourceProperties().getCurrentSchema() != null) {
            this.innerAfterEvents.add(
                    (sender, attrs) -> {
                        if(attrs.getConnection() != null) {
                            String curSchema = sender.getDataSourceProperties().getCurrentSchema().toUpperCase();
                            if(!Strings.isNullOrEmpty(curSchema)) {
                                LOG.debug("onAfterGetConnection - start setting current_schema=" + curSchema);
                                DbUtils.execSQL(attrs.getConnection(), "alter session set current_schema=" + curSchema);
                                LOG.debug("onAfterGetConnection - OK. current_schema now is " + curSchema);
                            }
                        }
                    }
            );
        }

        wrappers = new OraWrappersImpl(this.getDBMSName());
        DbUtils.getInstance().init(
                new OraTypeConverterImpl(),
                new OraUtilsImpl()
        );
    }

    @Override
    public String getDBMSName() {
        return "oracle";
    }

    @Override
    public SQLReader createReader(){
        return new OraReader();
    }

}
