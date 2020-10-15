package ru.bio4j.spring.database.pgsql;

import ru.bio4j.spring.commons.types.LogWrapper;
import ru.bio4j.spring.commons.utils.Strings;
import ru.bio4j.spring.database.api.SQLReader;
import ru.bio4j.spring.database.commons.DbContextAbstract;
import ru.bio4j.spring.database.commons.DbUtils;
import ru.bio4j.spring.model.BaseDataSourceProperties;

import javax.sql.DataSource;

public class PgSQLContext extends DbContextAbstract {
    private static final LogWrapper LOG = LogWrapper.getLogger(PgSQLContext.class);

    public PgSQLContext(final DataSource dataSource, final BaseDataSourceProperties dataSourceProperties) throws Exception {
        super(dataSource, dataSourceProperties);

        if(this.getDataSourceProperties().getCurrentSchema() != null) {
            this.innerAfterEvents.add(
                    (sender, attrs) -> {
                        if(attrs.getConnection() != null) {
                            String curSchema = sender.getDataSourceProperties().getCurrentSchema().toUpperCase();
                            if(!Strings.isNullOrEmpty(curSchema)) {
                                LOG.debug("onAfterGetConnection - start setting current_schema=" + curSchema);
                                DbUtils.execSQL(attrs.getConnection(), "SET search_path = " + curSchema);
                                LOG.debug("onAfterGetConnection - OK. current_schema now is " + curSchema);
                            }
                        }
                    }
            );
        }

        wrappers = new PgSQLWrappersImpl(this.getDBMSName());
        DbUtils.getInstance().init(
                new PgSQLTypeConverterImpl(),
                new PgSQLUtilsImpl()
        );
    }

    @Override
    public String getDBMSName() {
        return "pgsql";
    }

    @Override
    public SQLReader createReader(){
        return new PgSQLReader();
    }

}
