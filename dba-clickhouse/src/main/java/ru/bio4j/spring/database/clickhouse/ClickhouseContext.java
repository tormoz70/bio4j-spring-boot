package ru.bio4j.spring.database.clickhouse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.spring.database.api.*;
import ru.bio4j.spring.database.commons.DbContextAbstract;
import ru.bio4j.spring.database.commons.DbUtils;
import ru.bio4j.spring.model.config.props.DataSourceProperties;

import javax.sql.DataSource;

public class ClickhouseContext extends DbContextAbstract {
    private static final Logger LOG = LoggerFactory.getLogger(ClickhouseContext.class);

    public ClickhouseContext(final DataSource dataSource, final DataSourceProperties dataSourceProperties) {
        super(dataSource, dataSourceProperties);

        if(this.getDataSourceProperties().getCurrentSchema() != null) {
            this.innerAfterEvents.add(
                    new SQLConnectionConnectedEvent() {
                        @Override
                        public void handle(SQLContext sender, Attributes attrs) {
                            if(attrs.getConnection() != null) {
//                                String curSchema = sender.getDataSourceProperties().getCurrentSchema().toUpperCase();
//                                LOG.debug("onAfterGetConnection - start setting current_schema="+curSchema);
//                                DbUtils.execSQL(attrs.getConnection(), "alter session set current_schema="+curSchema);
//                                LOG.debug("onAfterGetConnection - OK. current_schema now is "+curSchema);
                            }
                        }
                    }
            );
        }

        wrappers = new ChWrappersImpl(this.dbmsName());
        DbUtils.getInstance().init(
                new ChTypeConverterImpl(),
                new ChUtilsImpl()
        );
    }

    @Override
    public StatementPreparerer createDbStatementPreparerer(SQLCursor cursor) {
        return new ChStatementPreparerer(cursor);
    }

    @Override
    public String dbmsName() {
        return "clickhouse";
    }



}
