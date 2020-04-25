package ru.bio4j.spring.database.clickhouse;

import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.spring.database.api.SQLConnectionConnectedEvent;
import ru.bio4j.spring.database.api.SQLContext;
import ru.bio4j.spring.database.api.SQLReader;
import ru.bio4j.spring.database.commons.DbContextAbstract;
import ru.bio4j.spring.database.commons.DbUtils;
import ru.bio4j.spring.model.transport.DataSourceProperties;

import javax.sql.DataSource;

@Component
public class ChContext extends DbContextAbstract {
    private static final Logger LOG = LoggerFactory.getLogger(ChContext.class);

    public ChContext(final DataSource cpool, final DataSourceProperties dataSourceProperties) {
        super(cpool, dataSourceProperties);

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

        wrappers = new ChWrappersImpl(this.getDBMSName());
        DbUtils.getInstance().init(
                new ChTypeConverterImpl(),
                new ChUtilsImpl()
        );
    }

    @Override
    public String getDBMSName() {
        return "clickhouse";
    }

    @Override
    public SQLReader createReader(){
        return new ChReader();
    }

}
