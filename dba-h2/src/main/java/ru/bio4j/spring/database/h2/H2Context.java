package ru.bio4j.spring.database.h2;

import ru.bio4j.spring.commons.types.LogWrapper;
import ru.bio4j.spring.database.api.*;
import ru.bio4j.spring.database.commons.DbContextAbstract;
import ru.bio4j.spring.database.commons.DbUtils;
import ru.bio4j.spring.model.config.props.DataSourceProperties;

import javax.sql.DataSource;

public class H2Context extends DbContextAbstract {
    private static final LogWrapper LOG = LogWrapper.getLogger(H2Context.class);

    private final DbServer dbServer;

    public H2Context(final DataSource cpool, final DataSourceProperties dataSourceProperties) {
        super(cpool, dataSourceProperties);

        dbServer = new H2ServerImpl(dataSourceProperties.getDbServerPort());

        if(this.getDataSourceProperties().getCurrentSchema() != null) {
            this.innerAfterEvents.add(
                    new SQLConnectionConnectedEvent() {
                        @Override
                        public void handle(SQLContext sender, Attributes attrs) {
                            if(attrs.getConnection() != null) {
                            }
                        }
                    }
            );
        }

        wrappers = new H2WrappersImpl(this.dbmsName());
        DbUtils.getInstance().init(
                new H2TypeConverterImpl(),
                new H2UtilsImpl()
        );
    }

    @Override
    public StatementPreparerer createDbStatementPreparerer(SQLCursor cursor) {
        return new H2StatementPreparerer(cursor);
    }

    @Override
    public String dbmsName() {
        return "h2";
    }

    @Override
    public DbServer getDbServer() {
        return dbServer;
    }


}
