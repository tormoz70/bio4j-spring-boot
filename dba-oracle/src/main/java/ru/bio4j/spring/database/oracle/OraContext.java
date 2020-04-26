package ru.bio4j.spring.database.oracle;

import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.spring.database.api.*;
import ru.bio4j.spring.database.commons.DbContextAbstract;
import ru.bio4j.spring.database.commons.DbStatementPreparerer;
import ru.bio4j.spring.database.commons.DbUtils;
import ru.bio4j.spring.model.transport.DataSourceProperties;

import javax.sql.DataSource;

@Component
public class OraContext extends DbContextAbstract {
    private static final Logger LOG = LoggerFactory.getLogger(OraContext.class);

    public OraContext(final DataSource cpool, final DataSourceProperties dataSourceProperties) {
        super(cpool, dataSourceProperties);

        if(this.getDataSourceProperties().getCurrentSchema() != null) {
            this.innerAfterEvents.add(
                    new SQLConnectionConnectedEvent() {
                        @Override
                        public void handle(SQLContext sender, Attributes attrs) {
                            if(attrs.getConnection() != null) {
                                String curSchema = sender.getDataSourceProperties().getCurrentSchema().toUpperCase();
                                LOG.debug("onAfterGetConnection - start setting current_schema="+curSchema);
                                DbUtils.execSQL(attrs.getConnection(), "alter session set current_schema="+curSchema);
                                LOG.debug("onAfterGetConnection - OK. current_schema now is "+curSchema);
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
